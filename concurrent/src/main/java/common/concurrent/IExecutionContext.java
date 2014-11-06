package common.concurrent;

/**
 * Data transfer object to keep any information within the separate threads
 */
public interface IExecutionContext {
    /**
     * @return object of type T from execution context
     */
    <T> T getObject(final String objectName);

    /**
     * @param objectName map key to save the object to context
     * @param object     any object to save
     */
    void setObject(final String objectName, final Object object);

    /**
     * @return current task executor to have possibility to invoke dependencies before dependent task execution
     */
    ITaskExecutor getExecutor();
}
