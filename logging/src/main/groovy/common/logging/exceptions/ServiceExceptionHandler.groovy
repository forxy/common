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

        if (t instanceof ServiceException) throw t

        String host = remoteServiceHost ?: 'N/A'

        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable cause = ExceptionUtils.getRootCause(t)
        if (cause instanceof ConnectException) {
            throw new ServiceException(LoggingCommonEvent.ServiceIsNotAvailable, host, cause.message)
        } else if (cause instanceof SocketTimeoutException) {
            throw new ServiceException(LoggingCommonEvent.ServiceTimeout, host, cause.message)
        } else {
            throw new ServiceException(LoggingCommonEvent.UnknownServiceException,
                    cause?.message ?: t?.message ?: 'N/A')
        }
    }
}
