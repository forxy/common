package common.rest.client.retry

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

public class SimpleRetryPolicy implements IRetryPolicy {
    private static final Log LOGGER = LogFactory.getLog(SimpleRetryPolicy.class)

    private RetryContext.IRetryContextFactory retryContextFactory = new RetryContext.DefaultRetryContextFactory()
    private final int maxAttempts
    private final int retryLatency
    private final long maxExecutionTime
    private final List<Class<?>> notRetriableExceptions = new ArrayList<Class<?>>()

    public SimpleRetryPolicy(final int maxAttempts, final int retryLatency) {
        this(maxAttempts, retryLatency, 0)
    }

    /**
     * @param maxAttempts Maximum number of attempts to perform before giving up
     * @param retryLatency Delay between retries
     * @param maxExecutionTime Maximum transaction time in milliseconds. 0 means unlimited time
     */
    public SimpleRetryPolicy(final int maxAttempts, final int retryLatency, final long maxExecutionTime) {
        this.maxAttempts = maxAttempts
        this.retryLatency = retryLatency
        this.maxExecutionTime = maxExecutionTime
    }

    public void setNotRetriableExceptions(final Class<?>[] notRetriableExceptions) {
        Collections.addAll(this.notRetriableExceptions, notRetriableExceptions)
    }

    @Override
    public IRetryContext open() {
        return retryContextFactory.newContext()
    }

    @SuppressWarnings('ThrowableResultOfMethodCallIgnored')
    @Override
    public boolean canRetry(final IRetryContext context) {
        if (context.getLastException() != null) {
            for (final Class<?> c : notRetriableExceptions) {
                if (c.isInstance(context.lastException)) {
                    return false
                }
            }
        }
        final boolean thereAreStillAttemptsLeft = context.getRetryCount() < maxAttempts
        // @formatter:off
        final boolean thereIsSomeTimeLeft = (maxExecutionTime <= 0) ||
                ((context.executionTime + retryLatency) < maxExecutionTime)
        // @formatter:on
        return thereAreStillAttemptsLeft && thereIsSomeTimeLeft
    }

    @Override
    public void registerException(final IRetryContext context, final Exception e) {
        LOGGER.warn("$e.message [attempt=$context.retryCount]", e)
        context.registerException(e)
    }

    @Override
    public void backOff(final IRetryContext context) {
        if (retryLatency > 0) {
            try {
                Thread.sleep(retryLatency)
            } catch (final InterruptedException e) {
                throw new RuntimeException(e)
            }
        }
    }

    public void setRetryContextFactory(final RetryContext.IRetryContextFactory retryContextFactory) {
        this.retryContextFactory = retryContextFactory
    }

    public IRetryPolicy withRetryContextFactory(final RetryContext.IRetryContextFactory retryContextFactory) {
        setRetryContextFactory(retryContextFactory)
        return this
    }
}
