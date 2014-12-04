package common.exceptions


import common.api.StatusEntity

/**
 * Base Client exception
 */
class ClientException extends ServiceException {

    StatusEntity errorEntity

    ClientException(StatusEntity errorEntity, EventLogBase eventLogID) {
        super(eventLogID)
        this.errorEntity = errorEntity
    }

    ClientException(StatusEntity errorEntity, EventLogBase eventLogID, Object... args) {
        super(eventLogID, args)
        this.errorEntity = errorEntity
    }

    ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogID, Object... args) {
        super(cause, eventLogID, args)
        this.errorEntity = errorEntity
    }

    ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogID) {
        super(cause, eventLogID)
        this.errorEntity = errorEntity
    }
}
