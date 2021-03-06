package common.exceptions

import org.slf4j.Logger

/**
 * Default exception implementation
 */
class ServiceException extends BaseServiceException {

    ServiceException(final EventLogBase eventLogID) {
        super(eventLogID)
    }

    ServiceException(final EventLogBase eventLogID, final Object... args) {
        super(eventLogID, args)
    }

    ServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(cause, eventLogID)
    }

    ServiceException(final Throwable cause, final EventLogBase eventLogID, final Object... args) {
        super(cause, eventLogID, args)
    }

    void log(final Logger logger) {
        if (isEnabledFor(logger, eventLogID.logLevel)) {
            ExceptionUtils.logEvent(logger, eventLogID, message, this, details)
        }
    }

    void log(final Logger logger, final String additionalInfo) {
        if (isEnabledFor(logger, eventLogID.logLevel)) {
            final StringBuilder message = new StringBuilder(message)
            if (null != additionalInfo) {
                message.append(additionalInfo)
            }
            ExceptionUtils.logEvent(logger, eventLogID, message.toString(), this, details)
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
