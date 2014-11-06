package common.exceptions;

/**
 * Event log id enumeration that describes common rest webservice specific events
 */
public enum HttpEventLogId implements EventLogBase
{
    // -------------------------------------------------------------------
    // Common events
    // -------------------------------------------------------------------

    InvalidClientInput(HttpEventLogId.BASE_EVENT_LOG_ID + 400, 400,
            "Operation is not allowed",
            EventType.InvalidInput),

    ValidationException(HttpEventLogId.BASE_EVENT_LOG_ID + 400, 400,
            "Validation rule violation. Details: %1$s",
            EventType.InvalidInput),

    Unauthorized(HttpEventLogId.BASE_EVENT_LOG_ID + 401, 401,
            "Operation is not allowed",
            EventType.InvalidInput),

    AccessDenied(HttpEventLogId.BASE_EVENT_LOG_ID + 404, 403,
            "Operation is not allowed",
            EventType.InvalidInput),

    ResourceNotFound(HttpEventLogId.BASE_EVENT_LOG_ID + 404, 404,
            "Operation is not allowed",
            EventType.InvalidInput),

    UnexpectedException(HttpEventLogId.BASE_EVENT_LOG_ID + 500, 500,
            "Unexpected unhandled exception. Details: %1$s",
                        EventType.InternalError),


    SSLConnectivityException(HttpEventLogId.BASE_EVENT_LOG_ID, 403,
            "Error during SSL communication. Details: %1$s",
            EventType.InvalidInput),

    SocketTimeoutException(HttpEventLogId.BASE_EVENT_LOG_ID + 501, 501,
            "Timeout. Details: %1$s",
            EventType.InternalError),

    ServiceUnavailableException(HttpEventLogId.BASE_EVENT_LOG_ID + 503, 503,
            "Service is not available. Details: %1$s",
            EventType.InternalError);



    public static final int BASE_EVENT_LOG_ID = 1000;

    private Level m_logLevel;
    private String m_formatString;
    private int m_eventId;
    private int m_responseId;
    private EventType m_eventType;

    private HttpEventLogId(final int eventId, final int responseId, final String formatString, final EventType eventType)
    {
        this(eventId, responseId, Level.ERROR, formatString, eventType);
    }

    private HttpEventLogId(final int eventId, final int responseId, final Level level, final String formatString,
                           final EventType eventType)
    {
        m_eventId = eventId;
        m_responseId = responseId;
        m_logLevel = level;
        m_formatString = formatString;
        m_eventType = eventType;
    }

    @Override
    public int getEventId()
    {
        return m_eventId;
    }

    public int getResponseId()
    {
        return m_responseId;
    }

    @Override
    public Level getLogLevel()
    {
        return m_logLevel;
    }

    @Override
    public EventType getEventType()
    {
        return m_eventType;
    }

    public String getMessage(final Object... arguments)
    {
        return arguments != null && arguments.length > 0 ? String.format(m_formatString, arguments) : m_formatString;
    }
}