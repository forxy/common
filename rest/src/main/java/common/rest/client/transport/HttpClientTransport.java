package common.rest.client.transport;

import common.rest.client.retry.IRetryPolicy;
import common.rest.client.transport.support.RepeatableEntityCreatingResponseInterceptor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import common.exceptions.ClientException;
import common.exceptions.HttpEventLogId;
import common.rest.client.retry.RetryExecutor;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation of the {@link ITransport} interface.
 */
public class HttpClientTransport implements ITransport {
    private static final HttpClientConfig CONFIG = new HttpClientConfig();

    private static final HttpHost PROXY = new HttpHost("127.0.0.1", 8888, "http");
    private final HttpClient httpClient;

    private IRetryPolicy m_retryPolicy;
    private boolean m_useRetries = false;

    /**
     * Constructs the Apache HTTP Client transport implementation with the default transport settings (connection
     * timeout, socket timeout etc.) without SSL connection support.
     */
    public HttpClientTransport() {
        this(null, CONFIG, false);
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the settings (connection/socket timeout, etc.)
     * provided in the <code>config</code> parameter
     *
     * @param config HTTP Client configuration
     */
    public HttpClientTransport(final HttpClientConfig config) {
        this(null, config, false);
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the default transport settings (connection
     * timeout, socket timeout etc.)
     *
     * @param keyStore <tt>keystory/truststore</tt> holder. Can be <b>null</b> when no SSL communication is required
     */
    public HttpClientTransport(final HttpClientSSLKeyStore keyStore) {
        this(keyStore, CONFIG, false);
    }

    /**
     * Constructs the Apache HTTP Client transport implementation with the settings (connection/socket timeout, etc.)
     * provided in the <code>config</code> parameter
     *
     * @param keyStore <tt>keystory/truststore</tt> holder. Can be <b>null</b> when no SSL communication is required
     * @param config   HTTP Client configuration
     */
    public HttpClientTransport(final HttpClientSSLKeyStore keyStore, final HttpClientConfig config) {
        this(keyStore, config, false);
    }

    /**
     * Constructs the Apache HTTP Client transport instance
     *
     * @param maxConnections                     maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis                socket timeout (read timeout)
     * @param connectionTimeoutMillis            connection timeout
     * @param connectionTTLMillis                maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled                  enable connection stale checking (disabled check may increase performance)
     */
    public HttpClientTransport(final int maxConnections, final int socketTimeoutMillis,
                               final int connectionTimeoutMillis, final int connectionTTLMillis,
                               final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                               final boolean staleCheckEnabled) {
        this(null, maxConnections, socketTimeoutMillis, connectionTimeoutMillis, connectionTTLMillis,
                connectionMonitorIdleTimeoutMillis, connectionMonitorRunIntervalMillis, staleCheckEnabled,
                null, null, false);
    }

    /**
     * Constructs the Apache HTTP Client transport instance
     *
     * @param maxConnections                     maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis                socket timeout (read timeout)
     * @param connectionTimeoutMillis            connection timeout
     * @param connectionTTLMillis                maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled                  enable connection stale checking (disabled check may increase performance)
     * @param requestInterceptor                 request interceptor (see perflog)
     * @param responseInterceptor                response interceptor (see perflog)
     * @param proxyEnabled                       If proxy should be enabled for the HTTP client ("127.0.0.1", 8888, "http")
     */
    public HttpClientTransport(final int maxConnections, final int socketTimeoutMillis,
                               final int connectionTimeoutMillis, final int connectionTTLMillis,
                               final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                               final boolean staleCheckEnabled, final HttpRequestInterceptor requestInterceptor,
                               final HttpResponseInterceptor responseInterceptor, final boolean proxyEnabled) {
        this(null, maxConnections, socketTimeoutMillis, connectionTimeoutMillis, connectionTTLMillis,
                connectionMonitorIdleTimeoutMillis, connectionMonitorRunIntervalMillis, staleCheckEnabled,
                requestInterceptor, responseInterceptor, proxyEnabled);
    }

    /**
     * Constructs the Apache HTTP Client transport implementation.
     *
     * @param keyStore                           SSL key store
     * @param maxConnections                     maximum http connection in pool (also per route as there is only one route)
     * @param socketTimeoutMillis                socket timeout (read timeout)
     * @param connectionTimeoutMillis            connection timeout
     * @param connectionTTLMillis                maximum time connection lives in pool an may be reused
     * @param connectionMonitorIdleTimeoutMillis maximum time connection may be idle in pool
     * @param connectionMonitorRunIntervalMillis interval of connection monitor periodic runs
     * @param staleCheckEnabled                  enable connection stale checking (disabled check may increase performance)
     * @param requestInterceptor                 request interceptor (see perflog)
     * @param responseInterceptor                response interceptor (see perflog)
     * @param proxyEnabled                       If proxy should be enabled for the HTTP client ("127.0.0.1", 8888, "http")
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public HttpClientTransport(final HttpClientSSLKeyStore keyStore, final int maxConnections,
                               final int socketTimeoutMillis, final int connectionTimeoutMillis, final int connectionTTLMillis,
                               final int connectionMonitorIdleTimeoutMillis, final int connectionMonitorRunIntervalMillis,
                               final boolean staleCheckEnabled, final HttpRequestInterceptor requestInterceptor,
                               final HttpResponseInterceptor responseInterceptor, final boolean proxyEnabled) {
        this(keyStore, new HttpClientConfig().setMaxConnections(maxConnections)
                .setSocketTimeoutMillis(socketTimeoutMillis)
                .setConnectionTimeoutMillis(connectionTimeoutMillis)
                .setConnectionTTLMillis(connectionTTLMillis)
                .setConnectionMonitorIdleTimeoutMillis(connectionMonitorIdleTimeoutMillis)
                .setConnectionMonitorRunIntervalMillis(connectionMonitorRunIntervalMillis)
                .setStaleCheckEnabled(staleCheckEnabled).setRequestInterceptor(requestInterceptor)
                .setResponseInterceptor(responseInterceptor), proxyEnabled);
    }

    /**
     * Constructs the Apache HTTP Client transport implementation.
     *
     * @param keyStore     SSL key store
     * @param config       Client configuration
     * @param proxyEnabled If proxy should be enabled for the HTTP client ("127.0.0.1", 8888, "http")
     */
    public HttpClientTransport(final HttpClientSSLKeyStore keyStore, final HttpClientConfig config,
                               final boolean proxyEnabled) {

        // Create a registry of custom connection socket factories for supported
        // protocol schemes / https
        LayeredConnectionSocketFactory socketFactory = keyStore != null ?
                keyStore.getSocketFactory() :
                SSLConnectionSocketFactory.getSystemSocketFactory();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connPoolControl =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connPoolControl.setMaxTotal(config.getMaxConnections());
        connPoolControl.setDefaultMaxPerRoute(config.getMaxConnections());


        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setConnectionManager(connPoolControl);

        clientBuilder.setSSLSocketFactory(socketFactory);
        clientBuilder.addInterceptorFirst(config.getRequestInterceptor());
        clientBuilder.addInterceptorFirst(new RepeatableEntityCreatingResponseInterceptor());
        clientBuilder.addInterceptorFirst(config.getResponseInterceptor());

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(config.getConnectionTimeoutMillis())
                .setSocketTimeout(config.getSocketTimeoutMillis())
                .setStaleConnectionCheckEnabled(config.isStaleCheckEnabled());

        if (proxyEnabled) {
            requestConfigBuilder.setProxy(PROXY);
        }

        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

        httpClient = clientBuilder.build();

        HttpClientConnectionMonitor.spawn(connPoolControl, config.getConnectionMonitorIdleTimeoutMillis(),
                config.getConnectionMonitorRunIntervalMillis());
    }

    @Override
    public <R, E> Response<R, E> performGet(final String url, final Map<String, String> headers,
                                            final IResponseHandler<R, E> responseHandler) throws ClientException {
        return processRequest(url, headers, null, responseHandler, HttpGet.METHOD_NAME);
    }

    @Override
    public <R, E> Response<R, E> performPost(final String url, final Map<String, String> headers,
                                             final String entity, final IResponseHandler<R, E> responseHandler) throws ClientException {
        return processRequest(url, headers, entity, responseHandler, HttpPost.METHOD_NAME);
    }

    @Override
    public <R, E> Response<R, E> performPut(final String url, final Map<String, String> headers,
                                            final String entity, final IResponseHandler<R, E> responseHandler) throws ClientException {
        return processRequest(url, headers, entity, responseHandler, HttpPut.METHOD_NAME);
    }

    @Override
    public <R, E> Response<R, E> performDelete(final String url, final Map<String, String> headers,
                                               final IResponseHandler<R, E> responseHandler) throws ClientException {
        return processRequest(url, headers, null, responseHandler, HttpDelete.METHOD_NAME);
    }

    private <R, E> Response<R, E> processRequest(final String url, final Map<String, String> headers,
                                                 final String entity, final IResponseHandler<R, E> responseHandler, final String method)
            throws ClientException {
        final HttpRequestBase request = initializeRequest(url, entity, method);
        populateRequestHeaders(headers, request);

        final HttpContext context = new BasicHttpContext();
        if (m_retryPolicy == null || !m_useRetries) {
            return executeRequest(httpClient, context, responseHandler, request);
        } else {
            final RetryExecutor<Response<R, E>, ClientException> retryExecutor =
                    new SimpleRetryExecutor<R, E>(httpClient, context, responseHandler, request);
            return retryExecutor.executeWithRetries(m_retryPolicy);
        }
    }

    private HttpRequestBase initializeRequest(final String url, final String entity, final String method)
            throws ClientException {
        HttpRequestBase request = null;
        if (HttpGet.METHOD_NAME.equals(method)) {
            request = new HttpGet(url);
        } else if (HttpPost.METHOD_NAME.equals(method) || HttpPut.METHOD_NAME.equals(method)) {
            request = HttpPost.METHOD_NAME.equals(method) ? new HttpPost(url) : new HttpPut(url);
            if (entity != null) {
                try {
                    final StringEntity params = new StringEntity(entity);
                    ((HttpEntityEnclosingRequestBase) request).setEntity(params);
                } catch (final UnsupportedEncodingException e) {
                    throw new ClientException(null, e, HttpEventLogId.UnexpectedException, e.getMessage());
                }
            }
        } else if (HttpDelete.METHOD_NAME.equals(method)) {
            request = new HttpDelete(url);
        } else {
            throw new ClientException(null, HttpEventLogId.UnexpectedException,
                    "Unknown HTTP method [" + method + "] while calling '" + url + "'");
        }
        return request;
    }

    private void populateRequestHeaders(final Map<String, String> headers, final HttpRequestBase request) {
        if (headers != null) {
            for (final Map.Entry<String, String> hv : headers.entrySet()) {
                request.setHeader(hv.getKey(), hv.getValue());
            }
        }
    }

    public void setRetryPolicy(final IRetryPolicy retryPolicy) {
        m_retryPolicy = retryPolicy;
    }

    public void setUseRetries(final boolean useRetries) {
        m_useRetries = useRetries;
    }

    private static <R, E> Response<R, E> executeRequest(final HttpClient httpClient,
                                                        final HttpContext httpContext, final IResponseHandler<R, E> responseHandler,
                                                        final HttpRequestBase request) throws ClientException {
        try {
            // @formatter:off
            return httpClient.execute(request, new ResponseHandler<Response<R, E>>() {
                @Override
                public Response<R, E> handleResponse(final HttpResponse response) throws IOException {
                    // @formatter:on
                    final StatusLine statusLine = response.getStatusLine();
                    final int statusCode = statusLine.getStatusCode();
                    final String statusReason = statusLine.getReasonPhrase();

                    // Populate response headers
                    final Map<String, String> responseHeaders = new HashMap<String, String>();
                    final Header[] allHeaders = response.getAllHeaders();
                    for (final Header header : allHeaders) {
                        responseHeaders.put(header.getName(), header.getValue());
                    }

                    // Ensure that the content has been fully consumed
                    final HttpEntity entity = response.getEntity();

                    // Retrieve resource from an entity
                    InputStream responseStream = null;
                    Response<R, E> resource = null;
                    try {
                        if (entity != null) {
                            responseStream = entity.getContent();
                        }
                        // @formatter:off
                        resource = responseHandler.handle(
                                statusCode, statusReason, responseHeaders, responseStream);
                        // @formatter:on
                    } finally {
                        IOUtils.closeQuietly(responseStream);
                        EntityUtils.consume(entity);
                    }
                    return resource;
                }
            }, httpContext);
        } catch (final SSLException e) {
            throw new ClientException(null, e, HttpEventLogId.SSLConnectivityException,
                    "[" + request.toString() + "]: " + e.getMessage());
        } catch (final java.net.SocketTimeoutException e) {
            throw new ClientException(null, e, HttpEventLogId.SocketTimeoutException,
                    "[" + request.toString() + "]: " + e.getMessage());
        } catch (final org.apache.http.conn.HttpHostConnectException e) {
            throw new ClientException(null, e, HttpEventLogId.ServiceUnavailableException,
                    "[" + request.toString() + "]: " + e.getMessage());
        } catch (final IOException e) {
            throw new ClientException(null, e, HttpEventLogId.ServiceUnavailableException,
                    "[" + request.toString() + "]: " + e.getMessage());
        }
    }

    private class SimpleRetryExecutor<R, E> extends RetryExecutor<Response<R, E>, ClientException> {
        private final HttpClient m_httpClient;
        private final HttpContext m_httpContext;
        private final IResponseHandler<R, E> m_responseHandler;
        private final HttpRequestBase m_request;

        SimpleRetryExecutor(final HttpClient httpClient, final HttpContext httpContext,
                            final IResponseHandler<R, E> responseHandler, final HttpRequestBase request) {
            m_httpClient = httpClient;
            m_httpContext = httpContext;
            m_responseHandler = responseHandler;
            m_request = request;
        }

        @Override
        protected Response<R, E> execute() throws ClientException {
            return executeRequest(m_httpClient, m_httpContext, m_responseHandler, m_request);
        }

        @Override
        protected ClientException postProcessLastException(final Exception lastException) {
            if (lastException instanceof ClientException) {
                return (ClientException) lastException;
            } else {
                return new ClientException(null, lastException, HttpEventLogId.UnexpectedException,
                        ExceptionUtils.getRootCauseMessage(lastException));
            }
        }
    }
}
