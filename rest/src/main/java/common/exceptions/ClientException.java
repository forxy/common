package common.exceptions;


import common.pojo.StatusEntity;

/**
 * Base Client exception
 */
public class ClientException extends ServiceException {

    private static final long serialVersionUID = -3213181048744156035L;

    private final StatusEntity errorEntity;

    public ClientException(StatusEntity errorEntity, String message, EventLogBase eventLogId) {
        super(message, eventLogId);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, EventLogBase eventLogId) {
        super(eventLogId);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, String message, EventLogBase eventLogId) {
        super(cause, message, eventLogId);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogId, Object... args) {
        super(cause, eventLogId, args);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, EventLogBase eventLogId, Object... args) {
        super(eventLogId, args);
        this.errorEntity = errorEntity;
    }

    public ClientException(StatusEntity errorEntity, Throwable cause, EventLogBase eventLogId) {
        super(cause, eventLogId);
        this.errorEntity = errorEntity;
    }

    public StatusEntity getErrorEntity() {
        return errorEntity;
    }
}
