package common.exceptions;

import org.slf4j.Logger;
import org.slf4j.MDC;

public final class ExceptionUtils
{
    private ExceptionUtils()
    {
    }

    /**
     * Returns the exception's entire nested stack trace.
     * 
     * @param throwable the <code>Throwable</code> to be examined
     * @return the nested stack trace, with the root cause first
     */
    public static String getFullStackTrace(final Throwable throwable)
    {
        return org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(throwable);
    }

    /**
     * @param throwable Throwable to scan for a particular cause
     * @param clazz Class of the cause exception to look for
     * @return Cause of the specified class if found, null otherwise
     */
    public static <T extends Throwable> T findCause(final Throwable throwable, final Class<T> clazz)
    {
        if (throwable == null)
        {
            return null;
        }
        if (clazz.isInstance(throwable))
        {
            return clazz.cast(throwable);
        }

        final Throwable cause = throwable.getCause();
        if (cause == throwable)
        {
            return null;
        }
        return findCause(cause, clazz);
    }

    public static <T extends EventLogBase> void logEvent(final Logger logger, final T event,
        final String message, final Throwable cause)
    {
        String record = "EventID=" + event.getEventId() + " EventCode=" + event + " " + getContext() + "\n" + message;
        switch (event.getLogLevel())
        {
            case ERROR:
                logger.error(record, cause);
                break;
            case WARN:
                logger.warn(record, cause);
                break;
            case INFO:
                logger.info(record, cause);
                break;
            case DEBUG:
                logger.debug(record, cause);
                break;
            case TRACE:
                logger.trace(record, cause);
                break;
        }
    }

    private static String getContext()
    {
        String prefix;
        try
        {
            prefix = MDC.getCopyOfContextMap().toString();
        }
        catch (final Exception e)
        {
            prefix = "Failed to retrieve NDC context: " + e.getMessage();
        }
        return prefix;
    }
}
