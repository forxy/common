package common.rest.client.transport;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;

/**
 * Utility class for configuring HttpClient
 */
public class HttpClientConfig
{
    public static final int DEFAULT_MAX_CONNECTIONS = 20;
    public static final int DEFAULT_CONN_TIMEOUT = 2000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int CONNECTION_TIME_TO_LIVE = 30000;
    public static final int DEFAULT_CONN_MONITOR_IDLE_TIMEOUT = 30000;
    public static final int DEFAULT_CONN_MONITOR_RUN_INTERVAL = 15000;
    public static final boolean DEFAULT_STALE_CHECK = true;

    private int m_maxConnections = DEFAULT_MAX_CONNECTIONS;
    private int m_connectionTimeoutMillis = DEFAULT_CONN_TIMEOUT;
    private int m_socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT;
    private int m_connectionTTLMillis = CONNECTION_TIME_TO_LIVE;
    private int m_connectionMonitorIdleTimeoutMillis = DEFAULT_CONN_MONITOR_IDLE_TIMEOUT;
    private int m_connectionMonitorRunIntervalMillis = DEFAULT_CONN_MONITOR_RUN_INTERVAL;
    private boolean m_staleCheckEnabled = DEFAULT_STALE_CHECK;
    private HttpRequestInterceptor m_requestInterceptor = null;
    private HttpResponseInterceptor m_responseInterceptor = null;

    public HttpClientConfig()
    {
    }

    public int getMaxConnections()
    {
        return m_maxConnections;
    }

    public HttpClientConfig setMaxConnections(final int maxConnections)
    {
        m_maxConnections = maxConnections;
        return this;
    }

    public int getSocketTimeoutMillis()
    {
        return m_socketTimeoutMillis;
    }

    public HttpClientConfig setSocketTimeoutMillis(final int socketTimeoutMillis)
    {
        m_socketTimeoutMillis = socketTimeoutMillis;
        return this;
    }

    public int getConnectionTimeoutMillis()
    {
        return m_connectionTimeoutMillis;
    }

    public HttpClientConfig setConnectionTimeoutMillis(final int connectionTimeoutMillis)
    {
        m_connectionTimeoutMillis = connectionTimeoutMillis;
        return this;
    }

    public int getConnectionTTLMillis()
    {
        return m_connectionTTLMillis;
    }

    public HttpClientConfig setConnectionTTLMillis(final int connectionTTLMillis)
    {
        m_connectionTTLMillis = connectionTTLMillis;
        return this;
    }

    public int getConnectionMonitorIdleTimeoutMillis()
    {
        return m_connectionMonitorIdleTimeoutMillis;
    }

    public HttpClientConfig setConnectionMonitorIdleTimeoutMillis(final int connectionMonitorIdleTimeoutMillis)
    {
        m_connectionMonitorIdleTimeoutMillis = connectionMonitorIdleTimeoutMillis;
        return this;
    }

    public int getConnectionMonitorRunIntervalMillis()
    {
        return m_connectionMonitorRunIntervalMillis;
    }

    public HttpClientConfig setConnectionMonitorRunIntervalMillis(final int connectionMonitorRunIntervalMillis)
    {
        m_connectionMonitorRunIntervalMillis = connectionMonitorRunIntervalMillis;
        return this;
    }

    public boolean isStaleCheckEnabled()
    {
        return m_staleCheckEnabled;
    }

    public HttpClientConfig setStaleCheckEnabled(final boolean staleCheckEnabled)
    {
        m_staleCheckEnabled = staleCheckEnabled;
        return this;
    }

    public HttpRequestInterceptor getRequestInterceptor()
    {
        return m_requestInterceptor;
    }

    public HttpClientConfig setRequestInterceptor(final HttpRequestInterceptor requestInterceptor)
    {
        m_requestInterceptor = requestInterceptor;
        return this;
    }

    public HttpResponseInterceptor getResponseInterceptor()
    {
        return m_responseInterceptor;
    }

    public HttpClientConfig setResponseInterceptor(final HttpResponseInterceptor responseInterceptor)
    {
        m_responseInterceptor = responseInterceptor;
        return this;
    }
}
