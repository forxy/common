package common.rest.client.transport

import org.apache.http.conn.HttpClientConnectionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

class HttpClientConnectionMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConnectionMonitor.class)

    private final HttpClientConnectionManager connectionManager
    private final long idleTimeoutMillis

    private HttpClientConnectionMonitor(final HttpClientConnectionManager connectionManager,
                                        final long idleTimeoutMillis) {
        this.connectionManager = connectionManager
        this.idleTimeoutMillis = idleTimeoutMillis
    }

    @Override
    void run() {
        try {
            connectionManager.closeExpiredConnections()
        } catch (e) {
            LOGGER.warn("Unable to close expired connections due to: $e.message", e)
        }
        if (idleTimeoutMillis > 0) {
            try {
                connectionManager.closeIdleConnections(idleTimeoutMillis, TimeUnit.MILLISECONDS)
            } catch (ex) {
                LOGGER.warn("Unable to close idle connections due to: $ex.message", ex)
            }
        }
    }

    static void spawn(final HttpClientConnectionManager connectionManager,
                      final long connectionMonitorIdleTimeoutMillis,
                      final long connectionMonitorRunIntervalMillis) {
        if (connectionMonitorRunIntervalMillis > 0) {
            final HttpClientConnectionMonitor idleConnectionMonitor =
                    new HttpClientConnectionMonitor(connectionManager, connectionMonitorIdleTimeoutMillis)
            Executors.newScheduledThreadPool(1, new ThreadFactory() {
                @Override
                public Thread newThread(final Runnable r) {
                    new Thread(target: r, daemon: true, contextClassLoader: null)
                }
            }).scheduleWithFixedDelay(idleConnectionMonitor, connectionMonitorRunIntervalMillis,
                    connectionMonitorRunIntervalMillis, TimeUnit.MILLISECONDS)
        }
    }
}
