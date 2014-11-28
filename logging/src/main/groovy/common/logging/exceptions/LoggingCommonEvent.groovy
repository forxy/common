package common.logging.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

enum LoggingCommonEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------
    ServiceIsNotAvailable(0, 500, EventType.InternalError,
            'Service at \'%s\' is not available. Please check the connection settings: %s'),

    ServiceTimeout(1, 500, EventType.InternalError,
            'The connection timeout exceeded for service at \'%s\'. Please check the connection settings.'),

    UnknownServiceException(2, 500, EventType.InternalError,
            'Internal error during database processing: %s'),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------
    DatabaseIsNotAvailable(100, 500, EventType.InternalError,
            'Database at \'%s\' is not available. Please check the connection settings: %s'),

    DatabaseTimeout(101, 500, EventType.InternalError,
            'The connection timeout exceeded for database at \'%s\'. Please check the connection settings.'),

    UnknownDataBaseException(102, 500, EventType.InternalError,
            'Unknown internal service error: %1$s')


    static final int BASE_EVENT_LOG_ID = 2000

    LoggingCommonEvent(final int eventID, final int responseID, final EventType eventType,
                            final String formatString) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, formatString)
    }

    LoggingCommonEvent(final int eventID, final int responseID, final EventLogBase.Level logLevel,
                            final EventType eventType, final String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}