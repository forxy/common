package common.rest.client.retry;

public interface IRetryContext {
    /**
     * @return Number of retries performed so far
     */
    int getRetryCount();

    /**
     * Registers an exception that is to be thrown if all retry attempts fail
     *
     * @param e An exceptino to be registered
     */
    void registerException(Exception e);

    /**
     * @return Last registered exception
     */
    Exception getLastException();

    /**
     * @return Number of milliseconds that have passed since context was opened
     */
    long getExcutionTime();
}
