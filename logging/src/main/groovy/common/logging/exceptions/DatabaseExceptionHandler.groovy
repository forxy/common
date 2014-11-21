package common.logging.exceptions

import common.exceptions.ServiceException
import common.logging.support.IExceptionHandler
import org.apache.commons.lang.exception.ExceptionUtils

/**
 * Class for handling exceptions connected with database
 */
class DatabaseExceptionHandler implements IExceptionHandler {

    String databaseHost = null

    @Override
    void handleException(final Throwable t) {
        String host = databaseHost != null ? databaseHost : 'N/A'

        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable cause = ExceptionUtils.getRootCause(t)
        if (cause instanceof ConnectException) {
            throw new ServiceException(LoggingCommonEventLogID.DatabaseIsNotAvailable, host,
                    cause.getMessage())
        } else if (cause instanceof SocketTimeoutException) {
            throw new ServiceException(LoggingCommonEventLogID.DatabaseTimeout, host, cause.getMessage())
        } else {
            throw new ServiceException(LoggingCommonEventLogID.UnknownDataBaseException,
                    cause != null ? cause.getMessage() : t != null ? t.getMessage() : 'N/A')
        }
    }
}