package common.rest.client.retry;

public class RetryContext implements IRetryContext {

    public static interface IRetryContextFactory {
        IRetryContext newContext();
    }

    public static final class DefaultRetryContextFactory implements IRetryContextFactory {
        @Override
        public IRetryContext newContext() {
            return new RetryContext();
        }
    }

    private int retryCount;
    private Exception lastException;
    private final long executionStartTime = System.currentTimeMillis();

    private RetryContext() {
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void registerException(final Exception e) {
        if (e != null) {
            lastException = e;
            retryCount++;
        }
    }

    @Override
    public Exception getLastException() {
        return lastException;
    }

    @Override
    public long getExcutionTime() {
        return System.currentTimeMillis() - executionStartTime;
    }
}
