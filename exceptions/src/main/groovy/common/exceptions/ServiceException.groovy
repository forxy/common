package common.exceptions

import org.slf4j.Logger

/**
 * Default exception implementation
 */
class ServiceException extends BaseServiceException {

    ServiceException(final EventLogBase eventLogID) {
        super(eventLogID)
    }

    ServiceException(final String message, final EventLogBase eventLogID) {
        super(message, eventLogID)
    }

    ServiceException(final Throwable cause, final String message, final EventLogBase eventLogID) {
        super(cause, message, eventLogID)
    }

    ServiceException(final Throwable cause, final EventLogBase eventLogID, final Object... args) {
        super(cause, eventLogID.getMessage(args), eventLogID)
    }

    ServiceException(final EventLogBase eventLogId, final Object... args) {
        super(eventLogId.getMessage(args), eventLogId)
    }

    ServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(cause, eventLogID)
    }

    void log(final Logger logger) {
        if (isEnabledFor(logger, getEventLogID().getLogLevel())) {
            ExceptionUtils.logEvent(logger, getEventLogID(), getMessage(), this)
        }
    }

    void log(final Logger logger, final String additionalInfo) {
        if (isEnabledFor(logger, getEventLogID().getLogLevel())) {
            final StringBuilder message = new StringBuilder(getMessage())
            if (null != additionalInfo) {
                message.append(additionalInfo)
            }
            ExceptionUtils.logEvent(logger, getEventLogID(), message.toString(), this)
        }
    }

    private static boolean isEnabledFor(Logger logger, EventLogBase.Level level) {
        if (logger != null) {
            switch (level) {
                case EventLogBase.Level.ERROR:
                    return logger.isErrorEnabled()
                case EventLogBase.Level.WARN:
                    return logger.isWarnEnabled()
                case EventLogBase.Level.INFO:
                    return logger.isInfoEnabled()
                case EventLogBase.Level.DEBUG:
                    return logger.isDebugEnabled()
                case EventLogBase.Level.TRACE:
                    return logger.isTraceEnabled()
                default:
                    return false
            }
        } else {
            return false
        }
    }
}
