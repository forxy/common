package common.exceptions

/**
 * Event log id enumeration that describes common rest webservice specific events
 */
enum HttpEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Common events
    // -------------------------------------------------------------------

    InvalidClientInput(400, 400, EventLogBase.EventType.InvalidInput,
            'Operation is not allowed'),

    ValidationException(400, 400, EventLogBase.EventType.InvalidInput,
            'Validation rule violation. Details: %1$s'),

    Unauthorized(401, 401, EventLogBase.EventType.InvalidInput,
            'Operation is not allowed'),

    AccessDenied(404, 403, EventLogBase.EventType.InvalidInput,
            'Operation is not allowed'),

    ResourceNotFound(404, 404, EventLogBase.EventType.InvalidInput,
            'Operation is not allowed'),

    UnexpectedException(500, 500, EventLogBase.EventType.InternalError,
            'Unexpected unhandled exception. Details: %1$s'),

    // -------------------------------------------------------------------
    // Network events
    // -------------------------------------------------------------------

    SSLConnectivity(0, 403, EventLogBase.EventType.InternalError,
    'Error during SSL communication. Details: %1$s'),

    SocketTimeout(501, 501, EventLogBase.EventType.InternalError,
            'Timeout. Details: %1$s'),

    ServiceUnavailable(503, 503, EventLogBase.EventType.InternalError,
            'Service is not available. Details: %1$s')

    private static final int BASE_EVENT_LOG_ID = 1000

    private HttpEvent(int eventID, int responseID, EventLogBase.EventType eventType, String formatString) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, formatString)
    }

    private HttpEvent(int eventID, int responseID, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                      String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}