package common.logging.exceptions

import common.exceptions.EventLogBase

enum LoggingCommonEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------

    ServiceIsNotAvailable(0, 500, EventLogBase.EventType.InternalError,
            'Service at \'%s\' is not available'),

    ServiceTimeout(1, 500, EventLogBase.EventType.InternalError,
            'The connection timeout exceeded for service at \'%s\''),

    UnknownServiceException(2, 500, EventLogBase.EventType.InternalError,
            'Internal error during database processing'),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------

    DatabaseIsNotAvailable(100, 500, EventLogBase.EventType.InternalError,
            'Database at \'%s\' is not available'),

    DatabaseTimeout(101, 500, EventLogBase.EventType.InternalError,
            'The connection timeout exceeded for database at \'%s\''),

    UnknownDataBaseException(102, 500, EventLogBase.EventType.InternalError,
            'Unknown internal service error')


    private static final int BASE_EVENT_LOG_ID = 2000

    private LoggingCommonEvent(final int eventID, final int responseID, final EventLogBase.EventType eventType,
                               final String messageFormat) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, messageFormat)
    }

    private LoggingCommonEvent(final int eventID, final int responseID, final EventLogBase.Level logLevel,
                               final EventLogBase.EventType eventType, final String messageFormat) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = responseID
        this.logLevel = logLevel
        this.messageFormat = messageFormat
        this.eventType = eventType
    }
}