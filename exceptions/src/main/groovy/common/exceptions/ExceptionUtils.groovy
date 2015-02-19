package common.exceptions

import org.slf4j.Logger
import org.slf4j.MDC

final class ExceptionUtils {

    ExceptionUtils() {
    }

    /**
     * @param throwable Throwable to scan for a particular cause
     * @param clazz Class of the cause exception to look for
     * @return Cause of the specified class if found, null otherwise
     */
    static <T extends Throwable> T findCause(final Throwable throwable, final Class<T> clazz) {
        if (throwable == null) {
            return null
        }
        if (clazz.isInstance(throwable)) {
            return clazz.cast(throwable)
        }

        final Throwable cause = throwable.cause
        if (cause == throwable) {
            return null
        }
        return findCause(cause, clazz)
    }

    static <T extends EventLogBase> void logEvent(final Logger logger, final T event, final String message,
                                                  final Throwable cause, final String details) {
        String record = "EventID=$event.eventID EventCode=$event" +
                "${details ? " Details=$details" : ''} $context\n $message"
        switch (event.logLevel) {
            case EventLogBase.Level.ERROR:
                logger.error(record, cause)
                break
            case EventLogBase.Level.WARN:
                logger.warn(record, cause)
                break
            case EventLogBase.Level.INFO:
                logger.info(record, cause)
                break
            case EventLogBase.Level.DEBUG:
                logger.debug(record, cause)
                break
            case EventLogBase.Level.TRACE:
                logger.trace(record, cause)
                break
        }
    }

    static String getContext() {
        String prefix
        try {
            prefix = MDC.copyOfContextMap as String
        } catch (final Exception e) {
            prefix = "Failed to retrieve NDC context: $e.message"
        }
        return prefix
    }
}
