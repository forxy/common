package common.rest.client.retry

class RetryContext implements IRetryContext {

    static interface IRetryContextFactory {
        IRetryContext newContext()
    }

    static final class DefaultRetryContextFactory implements IRetryContextFactory {
        @Override
        IRetryContext newContext() {
            return new RetryContext()
        }
    }

    int retryCount
    Exception lastException
    final long executionStartTime = System.currentTimeMillis()

    RetryContext() {
    }

    @Override
    void registerException(final Exception e) {
        if (e) {
            lastException = e
            retryCount++
        }
    }

    @Override
    long getExecutionTime() {
        return System.currentTimeMillis() - executionStartTime
    }
}
