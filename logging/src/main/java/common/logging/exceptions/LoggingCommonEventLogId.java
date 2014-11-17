package common.logging.exceptions;

import common.exceptions.EventLogBase;
import common.exceptions.EventType;

public enum LoggingCommonEventLogID implements EventLogBase {

    // -------------------------------------------------------------------
    // Service events
    // -------------------------------------------------------------------
    ServiceIsNotAvailable(LoggingCommonEventLogID.BASE_EVENT_LOG_ID, 500,
            "Service at '%s' is not available. Please check the connection settings: %s",
            EventType.InternalError),

    ServiceTimeout(LoggingCommonEventLogID.BASE_EVENT_LOG_ID + 1, 500,
            "The connection timeout exceeded for service at '%s'. Please check the connection settings.",
            EventType.InternalError),

    UnknownServiceException(LoggingCommonEventLogID.BASE_EVENT_LOG_ID + 2, 500,
            "Internal error during database processing: %s",
            EventType.InternalError),

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------
    DatabaseIsNotAvailable(LoggingCommonEventLogID.BASE_EVENT_LOG_ID + 100, 500,
            "Database at '%s' is not available. Please check the connection settings: %s",
            EventType.InternalError),

    DatabaseTimeout(LoggingCommonEventLogID.BASE_EVENT_LOG_ID + 101, 500,
            "The connection timeout exceeded for database at '%s'. Please check the connection settings.",
            EventType.InternalError),

    UnknownDataBaseException(LoggingCommonEventLogID.BASE_EVENT_LOG_ID + 102, 500,
            "Unknown internal service error: %1$s",
            EventType.InternalError);

    public static final int BASE_EVENT_LOG_ID = 2000;

    private Level logLevel;
    private String formatString;
    private int eventID;
    private int responseID;
    private EventType eventType;

    private LoggingCommonEventLogID(final int eventID, final int responseID, final String formatString,
                                    final EventType eventType) {
        this(eventID, responseID, Level.ERROR, formatString, eventType);
    }

    private LoggingCommonEventLogID(final int eventID, final int responseID, final Level logLevel,
                                    final String formatString, final EventType eventType) {
        this.eventID = eventID;
        this.responseID = responseID;
        this.logLevel = logLevel;
        this.formatString = formatString;
        this.eventType = eventType;
    }

    @Override
    public int getEventID() {
        return eventID;
    }

    public int getResponseID() {
        return responseID;
    }

    @Override
    public Level getLogLevel() {
        return logLevel;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString;
    }
}