package common.exceptions

trait EventLogBase {

    Level logLevel
    String messageFormat
    int eventID
    int httpCode
    EventType eventType

    public String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(messageFormat, arguments) : messageFormat
    }

    static enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    static enum EventType {

        InternalError(-2, 'Internal Error'),

        InvalidInput(-1, 'Invalid Input'),

        Success(0, 'Success'),

        BusinessLogicFailure(1, 'Business Logic Failure'),

        Warning(2, 'Warning')

        final int code

        final String description

        /**
         * Constructs a new instance initialized with an enum code.
         *
         * @param code The int code that uniquely identifies this enum value.
         * @param description The String description of code.
         */
        EventType(final int code, final String description) {
            this.code = code
            this.description = description
        }
    }
}
