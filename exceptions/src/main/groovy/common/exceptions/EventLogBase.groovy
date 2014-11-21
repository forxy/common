package common.exceptions

interface EventLogBase {

    int getEventID()

    int getResponseID()

    Level getLogLevel()

    EventType getEventType()

    String getMessage(final Object... arguments)

    enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }
}
