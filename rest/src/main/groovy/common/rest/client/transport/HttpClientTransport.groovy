package common.rest.client.transport

import common.exceptions.ClientException
import common.rest.client.retry.IRetryPolicy
import common.rest.client.retry.RetryExecutor
import common.rest.client.transport.support.RepeatableEntityCreatingResponseInterceptor
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.http.*
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.LayeredConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils

import javax.net.ssl.SSLException

import static common.exceptions.HttpEvent.*

/**
 * The default implementation of the {@link ITransport} interface.
 */
class HttpClientTransport implements ITransport {
    private static final HttpClientConfig CONFIG = new HttpClientConfig()

    private static final HttpHost PROXY = new HttpHost('127.0.0.1', 8888, 'http')
    private final HttpClient httpClient

    IRetryPolicy retryPolicy
    boolean useRetries = false

    /**
     * Constructs the Apache HTTP Client transport implementation with the default transport settings (connection
     * timeout, socket timeout etc.) without SSL connection support.
     */
    HttpClientTransport() {
        this(null, CONFIG, false)
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the settings (connection/socket timeout, etc.)
     * provided in the <code>config</code> parameter
     *
     * @param config HTTP Client configuration
     */
    HttpClientTransport(final HttpClientConfig config) {
        this(null, config, false)
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the default transport settings (connection
     * timeout, socket timeout etc.)
     *
     * @param keyStore <tt>keystory/truststore</tt> holder. Can be <b>null</b> when no SSL communication is required
     */
    HttpClientTransport(final HttpClientSSLKeyStore keyStore) {
        this(keyStore, CONFIG, false)
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the settings (connection/socket timeout, etc.)
     * provided in the <code>config</code> parameter
     *
     * @param keyStore <tt>keystory/truststore</tt> holder. Can be <b>null</b> when no SSL communication is required
     * @param config HTTP Client configuration
     */
    HttpClientTransport(final HttpClientSSLKeyStore keyStore, final HttpClientConfig config) {
        this(keyStore, config, false)
    }

    /**
     * Constructs the Apache HTTP Client transport instance
     *
     * @param maxConnections maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis socket timeout (read timeout)
     * @param connectionTimeoutMillis connection timeout
     * @param connectionTTLMillis maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled enable connection stale checking (disabled check may increase performance)
     */
    HttpClientTransport(final int maxConnections, final int socketTimeoutMillis,
                        final int connectionTimeoutMillis, final int connectionTTLMillis,
                        final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                        final boolean staleCheckEnabled) {
        this(null, maxConnections, socketTimeoutMillis, connectionTimeoutMillis, connectionTTLMillis,
                connectionMonitorIdleTimeoutMillis, connectionMonitorRunIntervalMillis, staleCheckEnabled,
                null, null, false)
    }

    /**
     * Constructs the Apache HTTP Client transport instance
     *
     * @param maxConnections maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis socket timeout (read timeout)
     * @param connectionTimeoutMillis connection timeout
     * @param connectionTTLMillis maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled enable connection stale checking (disabled check may increase performance)
     * @param requestInterceptor request interceptor (see perflog)
     * @param responseInterceptor response interceptor (see perflog)
     * @param proxyEnabled If proxy should be enabled for the HTTP client ('127.0.0.1', 8888, 'http')
     */
    HttpClientTransport(final int maxConnections, final int socketTimeoutMillis,
                        final int connectionTimeoutMillis, final int connectionTTLMillis,
                        final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                        final boolean staleCheckEnabled, final HttpRequestInterceptor requestInterceptor,
                        final HttpResponseInterceptor responseInterceptor, final boolean proxyEnabled) {
        this(null, maxConnections, socketTimeoutMillis, connectionTimeoutMillis, connectionTTLMillis,
                connectionMonitorIdleTimeoutMillis, connectionMonitorRunIntervalMillis, staleCheckEnabled,
                requestInterceptor, responseInterceptor, proxyEnabled)
    }

    /**
     * Constructs the Apache HTTP Client transport implementation.
     *
     * @param keyStore SSL key store
     * @param maxConnections maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis socket timeout (read timeout)
     * @param connectionTimeoutMillis connection timeout
     * @param connectionTTLMillis maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled enable connection stale checking (disabled check may increase performance)
     * @param requestInterceptor request interceptor (see perflog)
     * @param responseInterceptor response interceptor (see perflog)
     * @param proxyEnabled If proxy should be enabled for the HTTP client ('127.0.0.1', 8888, 'http')
     */
    @SuppressWarnings('PMD.ExcessiveParameterList')
    HttpClientTransport(final HttpClientSSLKeyStore keyStore, final int maxConnections,
                        final int socketTimeoutMillis, final int connectionTimeoutMillis, final int connectionTTLMillis,
                        final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                        final boolean staleCheckEnabled, final HttpRequestInterceptor requestInterceptor,
                        final HttpResponseInterceptor responseInterceptor, final boolean proxyEnabled) {
        this(keyStore, new HttpClientConfig()
                .setMaxConnections(maxConnections)
                .setSocketTimeoutMillis(socketTimeoutMillis)
                .setConnectionTimeoutMillis(connectionTimeoutMillis)
                .setConnectionTTLMillis(connectionTTLMillis)
                .setConnectionMonitorIdleTimeoutMillis(connectionMonitorIdleTimeoutMillis)
                .setConnectionMonitorRunIntervalMillis(connectionMonitorRunIntervalMillis)
                .setStaleCheckEnabled(staleCheckEnabled)
                .setRequestInterceptor(requestInterceptor)
                .setResponseInterceptor(responseInterceptor), proxyEnabled)
    }

    /**
     * Constructs the Apache HTTP Client transport implementation.
     *
     * @param keyStore SSL key store
     * @param config Client configuration
     * @param proxyEnabled If proxy should be enabled for the HTTP client ('127.0.0.1', 8888, 'http')
     */
    HttpClientTransport(final HttpClientSSLKeyStore keyStore, final HttpClientConfig config,
                        final boolean proxyEnabled) {

        // Create a registry of custom connection socket factories for supported
        // protocol schemes / https
        LayeredConnectionSocketFactory socketFactory = keyStore ?
                keyStore.socketFactory :
                SSLConnectionSocketFactory.systemSocketFactory
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register('https', socketFactory)
                .register('http', new PlainConnectionSocketFactory())
                .build()

        PoolingHttpClientConnectionManager connPoolControl =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry)
        connPoolControl.maxTotal = config.maxConnections
        connPoolControl.defaultMaxPerRoute = config.maxConnections


        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
        clientBuilder.connectionManager = connPoolControl

        clientBuilder.setSSLSocketFactory(socketFactory)
        clientBuilder.addInterceptorFirst(config.requestInterceptor)
        clientBuilder.addInterceptorFirst(new RepeatableEntityCreatingResponseInterceptor())
        clientBuilder.addInterceptorFirst(config.responseInterceptor)

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(config.connectionTimeoutMillis)
                .setSocketTimeout(config.socketTimeoutMillis)
                .setStaleConnectionCheckEnabled(config.staleCheckEnabled)

        if (proxyEnabled) {
            requestConfigBuilder.setProxy(PROXY)
        }

        clientBuilder.defaultRequestConfig = requestConfigBuilder.build()

        httpClient = clientBuilder.build()

        HttpClientConnectionMonitor.spawn(
                connPoolControl,
                config.connectionMonitorIdleTimeoutMillis,
                config.connectionMonitorRunIntervalMillis
        )
    }

    @Override
    public <R, E> ITransport.Response<R, E> performGet(final String url,
                                                       final Map<String, String> headers,
                                                       final ITransport.IResponseHandler<R, E> responseHandler)
            throws ClientException {
        return processRequest(url, headers, null, responseHandler, HttpGet.METHOD_NAME)
    }

    @Override
    public <R, E> ITransport.Response<R, E> performPost(final String url,
                                                        final Map<String, String> headers,
                                                        final String entity,
                                                        final ITransport.IResponseHandler<R, E> responseHandler)
            throws ClientException {
        return processRequest(url, headers, entity, responseHandler, HttpPost.METHOD_NAME)
    }

    @Override
    public <R, E> ITransport.Response<R, E> performPut(final String url,
                                                       final Map<String, String> headers,
                                                       final String entity,
                                                       final ITransport.IResponseHandler<R, E> responseHandler)
            throws ClientException {
        return processRequest(url, headers, entity, responseHandler, HttpPut.METHOD_NAME)
    }

    @Override
    public <R, E> ITransport.Response<R, E> performDelete(final String url,
                                                          final Map<String, String> headers,
                                                          final ITransport.IResponseHandler<R, E> responseHandler)
            throws ClientException {
        return processRequest(url, headers, null, responseHandler, HttpDelete.METHOD_NAME)
    }

    private <R, E> ITransport.Response<R, E> processRequest(final String url,
                                                            final Map<String, String> headers,
                                                            final String entity,
                                                            final ITransport.IResponseHandler<R, E> responseHandler,
                                                            final String method)
            throws ClientException {
        final HttpRequestBase request = initializeRequest(url, entity, method)
        populateRequestHeaders(headers, request)

        final HttpContext context = new BasicHttpContext()
        if (retryPolicy == null || !useRetries) {
            return executeRequest(httpClient, context, responseHandler, request)
        } else {
            final RetryExecutor<ITransport.Response<R, E>, ClientException> retryExecutor =
                    new SimpleRetryExecutor<R, E>(httpClient, context, responseHandler, request)
            return retryExecutor.executeWithRetries(retryPolicy)
        }
    }

    private static HttpRequestBase initializeRequest(final String url, final String entity, final String method)
            throws ClientException {
        HttpRequestBase request
        if (method == HttpGet.METHOD_NAME) {
            request = new HttpGet(url)
        } else if (method == HttpPost.METHOD_NAME || method == HttpPut.METHOD_NAME) {
            request = method == HttpPost.METHOD_NAME ? new HttpPost(url) : new HttpPut(url)
            if (entity) {
                try {
                    final StringEntity params = new StringEntity(entity)
                    ((HttpEntityEnclosingRequestBase) request).entity = params
                } catch (final UnsupportedEncodingException e) {
                    throw new ClientException(null, e, UnexpectedException, e.message)
                }
            }
        } else if (method == HttpDelete.METHOD_NAME) {
            request = new HttpDelete(url)
        } else {
            throw new ClientException(null, UnexpectedException, "Unknown HTTP method [$method] while calling '$url'")
        }
        return request
    }

    private static void populateRequestHeaders(final Map<String, String> headers, final HttpRequestBase request) {
        headers?.each { key, value ->
            request.setHeader(key, value)
        }
    }

    private static <R, E> ITransport.Response<R, E> executeRequest(
            final HttpClient httpClient,
            final HttpContext httpContext,
            final ITransport.IResponseHandler<R, E> responseHandler,
            final HttpRequestBase request) throws ClientException {
        try {
            return httpClient.execute(request, new ResponseHandler<ITransport.Response>() {
                @Override
                ITransport.Response handleResponse(final HttpResponse response) throws IOException {

                    final StatusLine statusLine = response.statusLine
                    final int statusCode = statusLine.statusCode
                    final String statusReason = statusLine.reasonPhrase

                    // Populate response headers
                    final Map<String, String> responseHeaders = new HashMap<String, String>()
                    response.allHeaders.each {
                        responseHeaders[it.name] = it.value
                    }

                    // Ensure that the content has been fully consumed
                    final HttpEntity entity = response.entity

                    // Retrieve resource from an entity
                    InputStream responseStream = null
                    ITransport.Response resource = null
                    try {
                        if (entity != null) {
                            responseStream = entity.content
                        }
                        resource = responseHandler.handle(statusCode, statusReason, responseHeaders, responseStream)
                    } finally {
                        IOUtils.closeQuietly(responseStream)
                        EntityUtils.consume(entity)
                    }
                    return resource
                }
            }, httpContext)
        } catch (final SSLException e) {
            throw new ClientException(null, e, SSLConnectivity, "[$request] $e.message")
        } catch (final SocketTimeoutException e) {
            throw new ClientException(null, e, SocketTimeout, "[$request] $e.message")
        } catch (final IOException e) {
            throw new ClientException(null, e, ServiceUnavailable, "[$request] $e.message")
        }
    }

    private class SimpleRetryExecutor<R, E> extends RetryExecutor<ITransport.Response<R, E>, ClientException> {
        private final HttpClient httpClient
        private final HttpContext httpContext
        private final ITransport.IResponseHandler<R, E> responseHandler
        private final HttpRequestBase request

        SimpleRetryExecutor(final HttpClient httpClient, final HttpContext httpContext,
                            final ITransport.IResponseHandler<R, E> responseHandler, final HttpRequestBase request) {
            this.httpClient = httpClient
            this.httpContext = httpContext
            this.responseHandler = responseHandler
            this.request = request
        }

        @Override
        protected ITransport.Response<R, E> execute() throws ClientException {
            return executeRequest(httpClient, httpContext, responseHandler, request)
        }

        @Override
        protected ClientException postProcessLastException(final Exception lastException) {
            if (lastException instanceof ClientException) {
                return (ClientException) lastException
            } else {
                return new ClientException(null, lastException, UnexpectedException,
                        ExceptionUtils.getRootCauseMessage(lastException))
            }
        }
    }
}
