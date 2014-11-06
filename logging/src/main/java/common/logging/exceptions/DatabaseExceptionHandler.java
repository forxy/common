package common.logging.exceptions;

import org.apache.commons.lang.exception.ExceptionUtils;
import common.exceptions.ServiceException;
import common.logging.support.IExceptionHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Class for handling exceptions connected with database
 */
public class DatabaseExceptionHandler implements IExceptionHandler {

    private String databaseHost = null;

    @Override
    public void handleException(final Throwable t) {
        String host = databaseHost != null ? databaseHost : "N/A";

        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable cause = ExceptionUtils.getRootCause(t);
        if (cause instanceof ConnectException) {
            throw new ServiceException(LoggingCommonEventLogId.DatabaseIsNotAvailable, host,
                    cause.getMessage());
        } else if (cause instanceof SocketTimeoutException) {
            throw new ServiceException(LoggingCommonEventLogId.DatabaseTimeout, host, cause.getMessage());
        } else {
            throw new ServiceException(LoggingCommonEventLogId.UnknownDataBaseException,
                    cause != null ? cause.getMessage() : t != null ? t.getMessage() : "N/A");
        }
    }

    public void setDatabaseHost(final String databaseHost) {
        this.databaseHost = databaseHost;
    }
}