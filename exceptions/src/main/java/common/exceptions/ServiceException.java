package common.exceptions;

import org.slf4j.Logger;

/**
 * Default exception implementation
 */
public class ServiceException extends BaseServiceException {

    private static final long serialVersionUID = -2523533558899886010L;

    public ServiceException(final EventLogBase eventLogId) {
        super(eventLogId);
    }

    public ServiceException(final String message, final EventLogBase eventLogId) {
        super(message, eventLogId);
    }

    public ServiceException(final Throwable cause, final String message, final EventLogBase eventLogId) {
        super(cause, message, eventLogId);
    }

    public ServiceException(final Throwable cause, final EventLogBase eventLogId, final Object... args) {
        super(cause, eventLogId.getMessage(args), eventLogId);
    }

    public ServiceException(final EventLogBase eventLogId, final Object... args) {
        super(eventLogId.getMessage(args), eventLogId);
    }

    public ServiceException(final Throwable cause, final EventLogBase eventLogId) {
        super(cause, eventLogId);
    }

    public void log(final Logger logger) {
        if (isEnabledFor(logger, getEventLogId().getLogLevel())) {
            ExceptionUtils.logEvent(logger, getEventLogId(), getMessage(), this);
        }
    }

    public void log(final Logger logger, final String additionalInfo) {
        if (isEnabledFor(logger, getEventLogId().getLogLevel())) {
            final StringBuilder message = new StringBuilder(getMessage());
            if (null != additionalInfo) {
                message.append(additionalInfo);
            }
            ExceptionUtils.logEvent(logger, getEventLogId(), message.toString(), this);
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
