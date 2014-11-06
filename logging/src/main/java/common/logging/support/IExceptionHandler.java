package common.logging.support;

/**
 * Exception handler for safe errors processing
 */
public interface IExceptionHandler {

    void handleException(Throwable t);
}
