package common.exceptions

/**
 * Base abstract exception class for all the Exceptions in the project
 * provides basic logic to handle error messages and status codes
 */
abstract class BaseServiceException extends RuntimeException {

    final EventLogBase eventLogID

    BaseServiceException(final String message, final EventLogBase eventLogID) {
        super(message)
        this.eventLogID = eventLogID
    }

    BaseServiceException(final EventLogBase eventLogID) {
        super(eventLogID.getMessage())
        this.eventLogID = eventLogID
    }

    BaseServiceException(final Throwable cause, final String message, final EventLogBase eventLogID) {
        super(message, cause)
        this.eventLogID = eventLogID
    }

    BaseServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(eventLogID.getMessage(cause.getMessage()), cause)
        this.eventLogID = eventLogID
    }

    EventLogBase getEventLogID() {
        return eventLogID
    }

    int getStatusCode() {
        return eventLogID.getEventID()
    }
}
