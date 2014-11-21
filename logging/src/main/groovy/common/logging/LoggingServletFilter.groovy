package common.logging

import common.exceptions.ServiceException
import common.logging.extractor.IHttpFieldExtractor
import common.logging.support.Fields
import common.logging.wrapper.HttpRequestWrapper
import common.logging.wrapper.HttpResponseWrapper
import common.support.Context
import common.support.SystemProperties

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static common.logging.LoggingServletFilter.Configs.IsHttpInfoLoggingEnabled
import static common.logging.support.Fields.*

/**
 * Request/Response logging filter
 */
class LoggingServletFilter extends AbstractPerformanceLogger implements Filter {

    static enum Configs {
        IsHttpInfoLoggingEnabled
    }

    boolean isHttpInfoLoggingEnabled = false

    @Override
    void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest rq = (HttpServletRequest) request
        final HttpServletResponse rs = (HttpServletResponse) response
        final String url = getRequestUrl(rq)
        if (isPerformanceLoggingEnabled) {
            final long timestampStart = System.currentTimeMillis()
            final long timestampStartNano = System.nanoTime()
            final HttpRequestWrapper brq = new HttpRequestWrapper(rq)
            final HttpResponseWrapper brs = new HttpResponseWrapper(rs)

            Context.push()
            try {
                //handle request, add request data to context
                handleRequest(brq, timestampStart, url)
                //chain downstream
                chain.doFilter(brq, brs)
            } catch (IOException | ServletException | ServiceException e) {
                processException(e)
                throw e
            } catch (Exception e) {
                processException(e)
                if (exceptionHandler != null) {
                    exceptionHandler.handleException(e)
                } else {
                    throw new ServletException(e)
                }
            } finally {
                //handle response, add response data to context
                handleResponse(brq, brs, timestampStart, timestampStartNano)
                Context.pop()
            }
        } else {
            chain.doFilter(rq, rs)
        }
    }

    void handleRequest(final HttpRequestWrapper rq, final long timestampStart, final String url) {
        Context.addGlobal(ProductName, SystemProperties.serviceName)
        Context.addGlobal(ActivityGUID, UUID.randomUUID().toString())
        Context.addFrame(ActivityName, activityName)
        Context.addFrame(ActivityStep, Fields.ActivitySteps.rq)
        Context.addFrame(TimestampStart, new Date(timestampStart))
        Context.addFrame(Timestamp, new Date(timestampStart))
        Context.addFrame(HostLocal, SystemProperties.hostAddress)
        Context.addFrame(HostRemote, rq.remoteAddr)

        if (requestFieldExtractors != null || isHttpInfoLoggingEnabled || isPayloadLoggingEnabled) {
            final Map<String, List<String>> rqHeaders = getHeaderMap(rq)
            //capture request http details
            if (isHttpInfoLoggingEnabled) {
                Context.addFrame(RequestURL, url)
                Context.addFrame(RequestMethod, rq.method)
                Context.addFrame(RequestHeaders, rqHeaders)
            }
            //extract request custom fields from payload
            final byte[] payload = rq.getRequestBody()
            requestFieldExtractors?.each {
                final Map<String, Object> frame = Context.peek().frame
                final Map<String, Object> extracted = it.extract(payload, frame, rq, null, rqHeaders, null)
                frame.putAll(extracted)
            }
            //capture request payload
            if (isPayloadLoggingEnabled) {
                Context.addFrame(RequestPayload, payload)
                writeFrame(requestPayloadWriter)
            }
        }
        //write request frame
        writeFrame(requestWriter)
    }

    void handleResponse(final HttpRequestWrapper rq, final HttpResponseWrapper rs, final long timestampStart,
                        final long timestampStartNano) {
        final long timestampEnd = System.currentTimeMillis()
        final long timestampEndNano = System.nanoTime()
        Context.addFrame(ActivityStep, Fields.ActivitySteps.rs)
        Context.addFrame(Timestamp, new Date(timestampEnd))
        Context.addFrame(TimestampEnd, new Date(timestampEnd))
        Context.addFrame(Duration, timestampEnd - timestampStart)
        Context.addFrame(DurationNano, (timestampEndNano - timestampStartNano) / 1000000)
        //capture response http details
        if (isHttpInfoLoggingEnabled) {
            Context.addFrame(ResponseStatus, rs.responseStatus)
            Context.addFrame(ResponseURL, rs.responseRedirectURL)
            Context.addFrame(ResponseHeaders, rs.responseHeaders)
        }
        if (isPayloadLoggingEnabled || responseFieldExtractors != null) {
            final byte[] payload = rs.responseBody
            //extract response custom fields from payload
            if (responseFieldExtractors != null) {
                for (final IHttpFieldExtractor fe : responseFieldExtractors) {
                    final Map<String, Object> frame = Context.peek().frame
                    final Map<String, Object> extracted =
                            fe.extract(payload, frame, rq, rs, getHeaderMap(rq), rs.responseHeaders)
                    frame.putAll(extracted)
                }
            }
            //capture response payload
            if (isPayloadLoggingEnabled) {
                Context.addFrame(ResponsePayload, payload)
                writeFrame(responsePayloadWriter)
            }
        }
        writeFrame(responseWriter)
    }

    static Map<String, List<String>> getHeaderMap(final HttpServletRequest rq) {
        Map<String, List<String>> result = null
        final Enumeration names = rq.headerNames
        if (names != null && names.hasMoreElements()) {
            result = new LinkedHashMap<>()
            while (names.hasMoreElements()) {
                final String name = (String) names.nextElement()

                @SuppressWarnings('unchecked')
                final Enumeration<String> values = rq.getHeaders(name)

                if (values != null) {
                    result[name] = Collections.list(values)
                }
            }
        }
        return result
    }

    static String getRequestUrl(final HttpServletRequest rq) {
        final StringBuilder url = new StringBuilder(256).append(rq.getRequestURL())
        if (rq.getQueryString() != null) {
            url.append('?').append(rq.getQueryString())
        }
        return url.toString()
    }

    @Override
    void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    void destroy() {
    }

    @Override
    void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet()
        if (configuration != null) {
            isHttpInfoLoggingEnabled = configuration.getBoolean(IsHttpInfoLoggingEnabled)
        }
    }
}