package common.exceptions

/**
 * Event log id enumeration that describes common rest webservice specific events
 */
enum HttpEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Common events
    // -------------------------------------------------------------------

    Success(200, 200, EventLogBase.Level.INFO, EventLogBase.EventType.Success,
            'Operation complete successfully'),

    InvalidClientInput(400, 400, EventLogBase.EventType.InvalidInput,
            'Invalid data provided'),

    ValidationException(400, 400, EventLogBase.EventType.InvalidInput,
            'Validation rule violation'),

    Unauthorized(401, 401, EventLogBase.EventType.InvalidInput,
            'Not Authenticated'),

    AccessDenied(404, 403, EventLogBase.EventType.InvalidInput,
            'Not enough permissions'),

    ResourceNotFound(404, 404, EventLogBase.EventType.InvalidInput,
            'Requested resource not found'),

    UnexpectedException(500, 500, EventLogBase.EventType.InternalError,
            'Unexpected error during request processing'),

    // -------------------------------------------------------------------
    // Network events
    // -------------------------------------------------------------------

    SSLConnectivity(0, 403, EventLogBase.EventType.InternalError,
            'Error during SSL communication'),

    SocketTimeout(501, 501, EventLogBase.EventType.InternalError,
            'Timeout'),

    ServiceUnavailable(503, 503, EventLogBase.EventType.InternalError,
            'Service is not available')

    private static final int BASE_EVENT_LOG_ID = 1000

    private HttpEvent(int eventID, int responseID, EventLogBase.EventType eventType, String messageFormat) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, messageFormat)
    }

    private HttpEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                      String messageFormat) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.messageFormat = messageFormat
        this.eventType = eventType
    }
}