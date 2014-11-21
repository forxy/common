package common.rest.client.retry;

public abstract class RetryExecutor<T, E extends Exception> {
    public T executeWithRetries(final IRetryPolicy retryPolicy) throws E {
        Exception lastException = null;
        final IRetryContext context = retryPolicy.open();
        while (retryPolicy.canRetry(context)) {
            try {
                return execute();
            } catch (final Exception e) {
                lastException = e;
                retryPolicy.registerException(context, e);
                if (retryPolicy.canRetry(context)) {
                    retryPolicy.backOff(context);
                }
            }
        }
        throw postProcessLastException(lastException);
    }

    protected abstract T execute() throws E;

    protected abstract E postProcessLastException(Exception lastException);
}
