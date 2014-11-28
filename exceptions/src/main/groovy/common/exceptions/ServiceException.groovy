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
        if (isEnabledFor(logger, eventLogID.logLevel)) {
            ExceptionUtils.logEvent(logger, eventLogID, message, this)
        }
    }

    void log(final Logger logger, final String additionalInfo) {
        if (isEnabledFor(logger, eventLogID.logLevel)) {
            final StringBuilder message = new StringBuilder(message)
            if (null != additionalInfo) {
                message.append(additionalInfo)
            }
            ExceptionUtils.logEvent(logger, eventLogID, message.toString(), this)
        }
    }

    private static boolean isEnabledFor(Logger logger, EventLogBase.Level level) {
        if (logger != null) {
            switch (level) {
                case EventLogBase.Level.ERROR:
                    return logger.errorEnabled
                case EventLogBase.Level.WARN:
                    return logger.warnEnabled
                case EventLogBase.Level.INFO:
                    return logger.infoEnabled
                case EventLogBase.Level.DEBUG:
                    return logger.debugEnabled
                case EventLogBase.Level.TRACE:
                    return logger.traceEnabled
                default:
                    return false
            }
        } else {
            return false
        }
    }
}
