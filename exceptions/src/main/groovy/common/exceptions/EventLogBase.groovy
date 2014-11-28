package common.exceptions

trait EventLogBase {

    EventLogBase.Level logLevel
    String formatString
    int eventID
    int responseID
    EventType eventType

    public String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString
    }

    static enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }
}
