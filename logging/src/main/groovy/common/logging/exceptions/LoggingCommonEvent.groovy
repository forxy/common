package common.logging.exceptions

import common.exceptions.EventLogBase

enum LoggingCommonEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------

    ServiceIsNotAvailable(0, 500, EventLogBase.EventType.InternalError,
            'Service at \'%s\' is not available. Please check the connection settings: %s'),

    ServiceTimeout(1, 500, EventLogBase.EventType.InternalError,
            'The connection timeout exceeded for service at \'%s\'. Please check the connection settings.'),

    UnknownServiceException(2, 500, EventLogBase.EventType.InternalError,
            'Internal error during database processing: %s'),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------

    DatabaseIsNotAvailable(100, 500, EventLogBase.EventType.InternalError,
    'Database at \'%s\' is not available. Please check the connection settings: %s'),

    DatabaseTimeout(101, 500, EventLogBase.EventType.InternalError,
            'The connection timeout exceeded for database at \'%s\'. Please check the connection settings.'),

    UnknownDataBaseException(102, 500, EventLogBase.EventType.InternalError,
            'Unknown internal service error: %1$s')


    private static final int BASE_EVENT_LOG_ID = 2000

    private LoggingCommonEvent(final int eventID, final int responseID, final EventLogBase.EventType eventType,
                               final String formatString) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, formatString)
    }

    private LoggingCommonEvent(final int eventID, final int responseID, final EventLogBase.Level logLevel,
                               final EventLogBase.EventType eventType, final String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}