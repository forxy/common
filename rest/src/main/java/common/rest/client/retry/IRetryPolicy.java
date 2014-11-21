package common.rest.client.retry;

public interface IRetryPolicy {
    IRetryContext open();

    boolean canRetry(IRetryContext context);

    void backOff(IRetryContext context);

    void registerException(IRetryContext context, Exception e);
}
