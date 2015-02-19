package common.exceptions

/**
 * Test events enumeration
 */
enum TestEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Test events
    // -------------------------------------------------------------------

    Trace(0, 500, EventLogBase.Level.TRACE, EventLogBase.EventType.BusinessLogicFailure, 'Trace with param: \'%1$s\''),

    Info(1, 200, EventLogBase.Level.INFO, EventLogBase.EventType.Success, 'Info with param: \'%1$s\''),

    Warn(2, 400, EventLogBase.Level.WARN, EventLogBase.EventType.Warning, 'Warn with param: \'%1$s\''),

    Error(3, 400, EventLogBase.EventType.InternalError, 'Error with params: \'%1$s\', \'%2$s\'')

    private static final int BASE_EVENT_LOG_ID = 1000

    private TestEvent(int eventID, int responseID, EventLogBase.EventType eventType, String messageFormat) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, messageFormat)
    }

    private TestEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType, String messageFormat) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.messageFormat = messageFormat
        this.eventType = eventType
    }
}
