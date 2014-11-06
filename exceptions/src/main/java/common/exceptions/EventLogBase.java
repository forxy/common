package common.exceptions;


public interface EventLogBase {
    int getEventId();

    int getResponseId();

    Level getLogLevel();

    EventType getEventType();

    String getMessage(final Object... arguments);

    public enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }
}
