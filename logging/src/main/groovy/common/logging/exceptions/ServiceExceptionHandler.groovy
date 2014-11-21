package common.logging.exceptions

import common.exceptions.ServiceException
import common.logging.support.IExceptionHandler
import org.apache.commons.lang.exception.ExceptionUtils

/**
 * Class for handling exceptions connected with remote service call or basic service implementation
 */
class ServiceExceptionHandler implements IExceptionHandler {

    String remoteServiceHost = null

    @Override
    void handleException(Throwable t) {
        String host = remoteServiceHost != null ? remoteServiceHost : 'N/A'

        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable cause = ExceptionUtils.getRootCause(t)
        if (cause instanceof ConnectException) {
            throw new ServiceException(LoggingCommonEventLogID.ServiceIsNotAvailable, host, cause.getMessage())
        } else if (cause instanceof SocketTimeoutException) {
            throw new ServiceException(LoggingCommonEventLogID.ServiceTimeout, host, cause.getMessage())
        } else {
            throw new ServiceException(LoggingCommonEventLogID.UnknownServiceException,
                    cause != null ? cause.getMessage() : t != null ? t.getMessage() : 'N/A')
        }
    }
}
