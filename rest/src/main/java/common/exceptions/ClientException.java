package common.exceptions;


import common.pojo.StatusEntity;

/**
 * Base Client exception
 */
public class ClientException extends ServiceException {

    private static final long serialVersionUID = -3213181048744156035L;

    private final StatusEntity errorEntity;

    public ClientException(StatusEntity errorEntity, String message, EventLogBase eventLogID) {
        super(message, eventLogID);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, EventLogBase eventLogID) {
        super(eventLogID);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, String message, EventLogBase eventLogID) {
        super(cause, message, eventLogID);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogID, Object... args) {
        super(cause, eventLogID, args);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, EventLogBase eventLogID, Object... args) {
        super(eventLogID, args);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogID) {
        super(cause, eventLogID);
        this.errorEntity = errorEntity;
    }

    public StatusEntity getErrorEntity() {
        return errorEntity;
    }
}
