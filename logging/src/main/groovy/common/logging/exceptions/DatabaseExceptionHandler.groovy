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
        String host = databaseHost ?: 'N/A'

        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable cause = ExceptionUtils.getRootCause(t)
        if (cause instanceof ConnectException) {
            throw new ServiceException(LoggingCommonEvent.DatabaseIsNotAvailable, host, cause.message)
        } else if (cause instanceof SocketTimeoutException) {
            throw new ServiceException(LoggingCommonEvent.DatabaseTimeout, host, cause.message)
        } else {
            throw new ServiceException(LoggingCommonEvent.UnknownDataBaseException,
                    cause?.message ?: t?.message ?: 'N/A')
        }
    }
}