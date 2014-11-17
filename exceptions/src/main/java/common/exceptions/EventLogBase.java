package common.exceptions;


public interface EventLogBase {

    int getEventID();

    int getResponseID();

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
