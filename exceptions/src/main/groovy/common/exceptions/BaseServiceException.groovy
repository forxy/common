package common.exceptions

/**
 * Base abstract exception class for all the Exceptions in the project
 * provides basic logic to handle error messages and status codes
 */
abstract class BaseServiceException extends RuntimeException {

    final EventLogBase eventLogID
    final String details

    BaseServiceException(final EventLogBase eventLogID) {
        super(eventLogID.getMessage())
        this.eventLogID = eventLogID
        this.details = null
    }

    BaseServiceException(final EventLogBase eventLogID, Object... args) {
        super(eventLogID.getMessage(args))
        this.eventLogID = eventLogID
        this.details = null
    }

    BaseServiceException(final Throwable cause, final EventLogBase eventLogID) {
        super(eventLogID.getMessage(), cause)
        this.eventLogID = eventLogID
        this.details = cause?.message
    }

    BaseServiceException(final Throwable cause, final EventLogBase eventLogID, Object... args) {
        super(eventLogID.getMessage(args), cause)
        this.eventLogID = eventLogID
        this.details = cause?.message
    }
}
