package common.rest.client.transport

import org.apache.http.HttpRequestInterceptor
import org.apache.http.HttpResponseInterceptor

/**
 * Utility class for configuring HttpClient
 */
class HttpClientConfig {

    static final int DEFAULT_MAX_CONNECTIONS = 20

    static final int DEFAULT_CONN_TIMEOUT = 2000

    static final int DEFAULT_SOCKET_TIMEOUT = 10000

    static final int CONNECTION_TIME_TO_LIVE = 30000

    static final int DEFAULT_CONN_MONITOR_IDLE_TIMEOUT = 30000

    static final int DEFAULT_CONN_MONITOR_RUN_INTERVAL = 15000

    static final boolean DEFAULT_STALE_CHECK = true


    int maxConnections = DEFAULT_MAX_CONNECTIONS

    int connectionTimeoutMillis = DEFAULT_CONN_TIMEOUT

    int socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT

    int connectionTTLMillis = CONNECTION_TIME_TO_LIVE

    int connectionMonitorIdleTimeoutMillis = DEFAULT_CONN_MONITOR_IDLE_TIMEOUT

    int connectionMonitorRunIntervalMillis = DEFAULT_CONN_MONITOR_RUN_INTERVAL

    boolean staleCheckEnabled = DEFAULT_STALE_CHECK

    HttpRequestInterceptor requestInterceptor = null

    HttpResponseInterceptor responseInterceptor = null

    HttpClientConfig setMaxConnections(final int maxConnections) {
        this.maxConnections = maxConnections
        return this
    }

    HttpClientConfig setSocketTimeoutMillis(final int socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis
        return this
    }

    HttpClientConfig setConnectionTimeoutMillis(final int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis
        return this
    }

    HttpClientConfig setConnectionTTLMillis(final int connectionTTLMillis) {
        this.connectionTTLMillis = connectionTTLMillis
        return this
    }

    HttpClientConfig setConnectionMonitorIdleTimeoutMillis(final int connectionMonitorIdleTimeoutMillis) {
        this.connectionMonitorIdleTimeoutMillis = connectionMonitorIdleTimeoutMillis
        return this
    }

    HttpClientConfig setConnectionMonitorRunIntervalMillis(final int connectionMonitorRunIntervalMillis) {
        this.connectionMonitorRunIntervalMillis = connectionMonitorRunIntervalMillis
        return this
    }

    HttpClientConfig setStaleCheckEnabled(final boolean staleCheckEnabled) {
        this.staleCheckEnabled = staleCheckEnabled
        return this
    }

    HttpClientConfig setRequestInterceptor(final HttpRequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor
        return this
    }

    HttpClientConfig setResponseInterceptor(final HttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor
        return this
    }
}
