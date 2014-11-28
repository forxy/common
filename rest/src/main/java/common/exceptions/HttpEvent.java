package common.exceptions;

/**
 * Event log id enumeration that describes common rest webservice specific events
 */
public enum HttpEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Common events
    // -------------------------------------------------------------------

    InvalidClientInput(HttpEvent.BASE_EVENT_LOG_ID + 400, 400,
            "Operation is not allowed",
            EventType.InvalidInput),

    ValidationException(HttpEvent.BASE_EVENT_LOG_ID + 400, 400,
            "Validation rule violation. Details: %1$s",
            EventType.InvalidInput),

    Unauthorized(HttpEvent.BASE_EVENT_LOG_ID + 401, 401,
            "Operation is not allowed",
            EventType.InvalidInput),

    AccessDenied(HttpEvent.BASE_EVENT_LOG_ID + 404, 403,
            "Operation is not allowed",
            EventType.InvalidInput),

    ResourceNotFound(HttpEvent.BASE_EVENT_LOG_ID + 404, 404,
            "Operation is not allowed",
            EventType.InvalidInput),

    UnexpectedException(HttpEvent.BASE_EVENT_LOG_ID + 500, 500,
            "Unexpected unhandled exception. Details: %1$s",
            EventType.InternalError),

    // -------------------------------------------------------------------
    // Network events
    // -------------------------------------------------------------------

    SSLConnectivityException(HttpEvent.BASE_EVENT_LOG_ID, 403,
            "Error during SSL communication. Details: %1$s",
            EventType.InvalidInput),

    SocketTimeoutException(HttpEvent.BASE_EVENT_LOG_ID + 501, 501,
            "Timeout. Details: %1$s",
            EventType.InternalError),

    ServiceUnavailableException(HttpEvent.BASE_EVENT_LOG_ID + 503, 503,
            "Service is not available. Details: %1$s",
            EventType.InternalError);


    public static final int BASE_EVENT_LOG_ID = 1000;

    private Level logLevel;
    private String formatString;
    private int eventID;
    private int responseID;
    private EventType eventType;

    private HttpEvent(final int eventID, final int responseID, final String formatString, final EventType eventType) {
        this(eventID, responseID, Level.ERROR, formatString, eventType);
    }

    private HttpEvent(final int eventID, final int responseID, final Level logLevel, final String formatString,
                      final EventType eventType) {
        this.eventID = eventID;
        this.responseID = responseID;
        this.logLevel = logLevel;
        this.formatString = formatString;
        this.eventType = eventType;
    }

    @Override
    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(final Level logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String getFormatString() {
        return formatString;
    }

    @Override
    public void setFormatString(final String formatString) {
        this.formatString = formatString;
    }

    @Override
    public int getEventID() {
        return eventID;
    }

    @Override
    public void setEventID(final int eventID) {
        this.eventID = eventID;
    }

    @Override
    public int getResponseID() {
        return responseID;
    }

    @Override
    public void setResponseID(final int responseID) {
        this.responseID = responseID;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(final EventType eventType) {
        this.eventType = eventType;
    }

    public String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString;
    }
}