package common.exceptions;

import org.slf4j.Logger;

/**
 * Default exception implementation
 */
public class ServiceException extends BaseServiceException {

    private static final long serialVersionUID = -2523533558899886010L;

    public ServiceException(final EventLogBase eventLogID) {
        super(eventLogID);
    }

    public ServiceException(final String message, final EventLogBase eventLogID) {
        super(message, eventLogID);
    }

    public ServiceException(final Throwable cause, final String message, final EventLogBase eventLogID) {
        super(cause, message, eventLogID);
    }

    public ServiceException(final Throwable cause, final EventLogBase eventLogID, final Object... args) {
        super(cause, eventLogID.getMessage(args), eventLogID);
    }

    public ServiceException(final EventLogBase eventLogId, final Object... args) {
        super(eventLogId.getMessage(args), eventLogId);
    }

    public ServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(cause, eventLogID);
    }

    public void log(final Logger logger) {
        if (isEnabledFor(logger, getEventLogID().getLogLevel())) {
            ExceptionUtils.logEvent(logger, getEventLogID(), getMessage(), this);
        }
    }

    public void log(final Logger logger, final String additionalInfo) {
        if (isEnabledFor(logger, getEventLogID().getLogLevel())) {
            final StringBuilder message = new StringBuilder(getMessage());
            if (null != additionalInfo) {
                message.append(additionalInfo);
            }
            ExceptionUtils.logEvent(logger, getEventLogID(), message.toString(), this);
        }
    }

    private boolean isEnabledFor(Logger logger, EventLogBase.Level level) {
        if (logger != null) {
            switch (level) {
                case ERROR:
                    return logger.isErrorEnabled();
                case WARN:
                    return logger.isWarnEnabled();
                case INFO:
                    return logger.isInfoEnabled();
                case DEBUG:
                    return logger.isDebugEnabled();
                case TRACE:
                    return logger.isTraceEnabled();
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}
