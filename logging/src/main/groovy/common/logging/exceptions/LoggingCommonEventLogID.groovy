package common.logging.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

enum LoggingCommonEventLogID implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------
    ServiceIsNotAvailable(BASE_EVENT_LOG_ID, 500,
            'Service at \'%s\' is not available. Please check the connection settings: %s',
            EventType.InternalError),

    ServiceTimeout(BASE_EVENT_LOG_ID + 1, 500,
            'The connection timeout exceeded for service at \'%s\'. Please check the connection settings.',
            EventType.InternalError),

    UnknownServiceException(BASE_EVENT_LOG_ID + 2, 500,
            'Internal error during database processing: %s',
            EventType.InternalError),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------
    DatabaseIsNotAvailable(BASE_EVENT_LOG_ID + 100, 500,
            'Database at \'%s\' is not available. Please check the connection settings: %s',
            EventType.InternalError),

    DatabaseTimeout(BASE_EVENT_LOG_ID + 101, 500,
            'The connection timeout exceeded for database at \'%s\'. Please check the connection settings.',
            EventType.InternalError),

    UnknownDataBaseException(BASE_EVENT_LOG_ID + 102, 500,
            'Unknown internal service error: %1$s',
            EventType.InternalError)

    static final int BASE_EVENT_LOG_ID = 2000

    EventLogBase.Level logLevel
    String formatString
    int eventID
    int responseID
    EventType eventType

    LoggingCommonEventLogID(final int eventID, final int responseID, final String formatString,
                            final EventType eventType) {
        this(eventID, responseID, EventLogBase.Level.ERROR, formatString, eventType)
    }

    LoggingCommonEventLogID(final int eventID, final int responseID, final EventLogBase.Level logLevel,
                            final String formatString, final EventType eventType) {
        this.eventID = eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }

    String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString
    }
}