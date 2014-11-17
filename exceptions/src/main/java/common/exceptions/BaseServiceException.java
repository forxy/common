package common.exceptions;

/**
 * Base abstract exception class for all the Exceptions in the project
 * provides basic logic to handle error messages and status codes
 */
public abstract class BaseServiceException extends RuntimeException {

    private static final long serialVersionUID = -6662646336105989846L;

    private final EventLogBase eventLogID;

    public BaseServiceException(final String message, final EventLogBase eventLogID) {
        super(message);
        this.eventLogID = eventLogID;
    }

    public BaseServiceException(final EventLogBase eventLogID) {
        super(eventLogID.getMessage());
        this.eventLogID = eventLogID;
    }

    public BaseServiceException(final Throwable cause, final String message, final EventLogBase eventLogID) {
        super(message, cause);
        this.eventLogID = eventLogID;
    }

    public BaseServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(eventLogID.getMessage(cause.getMessage()), cause);
        this.eventLogID = eventLogID;
    }

    public EventLogBase getEventLogID() {
        return eventLogID;
    }

    public int getStatusCode() {
        return eventLogID.getEventID();
    }
}
