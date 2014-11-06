package common.logging.exceptions;

import common.exceptions.EventLogBase;
import common.exceptions.EventType;

public enum LoggingCommonEventLogId implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------
    ServiceIsNotAvailable(LoggingCommonEventLogId.BASE_EVENT_LOG_ID, 500,
            "Service at '%s' is not available. Please check the connection settings: %s",
            EventType.InternalError),

    ServiceTimeout(LoggingCommonEventLogId.BASE_EVENT_LOG_ID + 1, 500,
            "The connection timeout exceeded for service at '%s'. Please check the connection settings.",
            EventType.InternalError),

    UnknownServiceException(LoggingCommonEventLogId.BASE_EVENT_LOG_ID + 2, 500,
            "Internal error during database processing: %s",
            EventType.InternalError),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------
    DatabaseIsNotAvailable(LoggingCommonEventLogId.BASE_EVENT_LOG_ID + 100, 500,
            "Database at '%s' is not available. Please check the connection settings: %s",
            EventType.InternalError),

    DatabaseTimeout(LoggingCommonEventLogId.BASE_EVENT_LOG_ID + 101, 500,
            "The connection timeout exceeded for database at '%s'. Please check the connection settings.",
            EventType.InternalError),

    UnknownDataBaseException(LoggingCommonEventLogId.BASE_EVENT_LOG_ID + 102, 500,
            "Unknown internal service error: %1$s",
            EventType.InternalError);

    public static final int BASE_EVENT_LOG_ID = 2000;

    private Level m_logLevel;
    private String m_formatString;
    private int m_eventId;
    private int m_responseId;
    private EventType m_eventType;

    private LoggingCommonEventLogId(final int eventId, final int responseId, final String formatString, final EventType eventType) {
        this(eventId, responseId, Level.ERROR, formatString, eventType);
    }

    private LoggingCommonEventLogId(final int eventId, final int responseId, final Level level, final String formatString,
                                    final EventType eventType) {
        m_eventId = eventId;
        m_responseId = responseId;
        m_logLevel = level;
        m_formatString = formatString;
        m_eventType = eventType;
    }

    @Override
    public int getEventId() {
        return m_eventId;
    }

    public int getResponseId() {
        return m_responseId;
    }

    @Override
    public Level getLogLevel() {
        return m_logLevel;
    }

    @Override
    public EventType getEventType() {
        return m_eventType;
    }

    public String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(m_formatString, arguments) : m_formatString;
    }
}