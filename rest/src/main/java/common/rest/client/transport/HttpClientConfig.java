package common.rest.client.transport;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;

/**
 * Utility class for configuring HttpClient
 */
public class HttpClientConfig {

    public static final int DEFAULT_MAX_CONNECTIONS = 20;

    public static final int DEFAULT_CONN_TIMEOUT = 2000;

    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;

    public static final int CONNECTION_TIME_TO_LIVE = 30000;

    public static final int DEFAULT_CONN_MONITOR_IDLE_TIMEOUT = 30000;

    public static final int DEFAULT_CONN_MONITOR_RUN_INTERVAL = 15000;

    public static final boolean DEFAULT_STALE_CHECK = true;


    private int maxConnections = DEFAULT_MAX_CONNECTIONS;

    private int connectionTimeoutMillis = DEFAULT_CONN_TIMEOUT;

    private int socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT;

    private int connectionTTLMillis = CONNECTION_TIME_TO_LIVE;

    private int connectionMonitorIdleTimeoutMillis = DEFAULT_CONN_MONITOR_IDLE_TIMEOUT;

    private int connectionMonitorRunIntervalMillis = DEFAULT_CONN_MONITOR_RUN_INTERVAL;

    private boolean staleCheckEnabled = DEFAULT_STALE_CHECK;

    private HttpRequestInterceptor requestInterceptor = null;

    private HttpResponseInterceptor responseInterceptor = null;


    public HttpClientConfig() {
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public HttpClientConfig setMaxConnections(final int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public int getSocketTimeoutMillis() {
        return socketTimeoutMillis;
    }

    public HttpClientConfig setSocketTimeoutMillis(final int socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis;
        return this;
    }

    public int getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    public HttpClientConfig setConnectionTimeoutMillis(final int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        return this;
    }

    public int getConnectionTTLMillis() {
        return connectionTTLMillis;
    }

    public HttpClientConfig setConnectionTTLMillis(final int connectionTTLMillis) {
        this.connectionTTLMillis = connectionTTLMillis;
        return this;
    }

    public int getConnectionMonitorIdleTimeoutMillis() {
        return connectionMonitorIdleTimeoutMillis;
    }

    public HttpClientConfig setConnectionMonitorIdleTimeoutMillis(final int connectionMonitorIdleTimeoutMillis) {
        this.connectionMonitorIdleTimeoutMillis = connectionMonitorIdleTimeoutMillis;
        return this;
    }

    public int getConnectionMonitorRunIntervalMillis() {
        return connectionMonitorRunIntervalMillis;
    }

    public HttpClientConfig setConnectionMonitorRunIntervalMillis(final int connectionMonitorRunIntervalMillis) {
        this.connectionMonitorRunIntervalMillis = connectionMonitorRunIntervalMillis;
        return this;
    }

    public boolean isStaleCheckEnabled() {
        return staleCheckEnabled;
    }

    public HttpClientConfig setStaleCheckEnabled(final boolean staleCheckEnabled) {
        this.staleCheckEnabled = staleCheckEnabled;
        return this;
    }

    public HttpRequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public HttpClientConfig setRequestInterceptor(final HttpRequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
        return this;
    }

    public HttpResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }

    public HttpClientConfig setResponseInterceptor(final HttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
        return this;
    }
}
