package common.logging;

import common.logging.support.Fields;
import common.logging.support.MetadataHelper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import common.exceptions.ServiceException;
import common.support.Context;
import common.support.SystemProperties;

import java.util.Date;
import java.util.UUID;

/**
 * Method interceptor for payload and performance logging
 */
public class LoggingInterceptor extends AbstractPerformanceLogger implements MethodInterceptor {

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (isPerformanceLoggingEnabled) {
            final long timestampStart = System.currentTimeMillis();
            final long timestampStartNano = System.nanoTime();
            Context.push();
            try {
                if (!Context.contains(Fields.ProductName)) {
                    Context.addGlobal(Fields.ProductName, SystemProperties.getServiceName());
                }
                if (!Context.contains(Fields.ActivityGUID)) {
                    Context.addGlobal(Fields.ActivityGUID, UUID.randomUUID().toString());
                }
                Context.addFrame(Fields.ActivityName,
                        activityName != null ? activityName : MetadataHelper.getRealClassName(invocation.getThis()));
                Context.addFrame(Fields.ActivityStep, Fields.ActivitySteps.rq);
                Context.addFrame(Fields.TimestampStart, new Date(timestampStart));
                Context.addFrame(Fields.Timestamp, new Date(timestampStart));
                if (requestWriter != null) {
                    requestWriter.log(Context.peek());
                }
                return invocation.proceed();
            } catch (ServiceException se) {
                processException(se);
                throw se;
            } catch (final Exception e) {
                processException(e);
                if (exceptionHandler != null) {
                    exceptionHandler.handleException(e);
                    return null;
                } else {
                    throw e;
                }
            } finally {
                final long timestampEndNano = System.nanoTime();
                final long timestampEnd = System.currentTimeMillis();
                Context.addFrame(Fields.ActivityStep, Fields.ActivitySteps.rs);
                if (!Context.contains(Fields.OperationName)) {
                    Context.addFrame(Fields.OperationName, invocation.getMethod().getName());
                }
                Context.addFrame(Fields.Timestamp, new Date(timestampEnd));
                Context.addFrame(Fields.TimestampEnd, new Date(timestampEnd));
                Context.addFrame(Fields.Duration, timestampEnd - timestampStart);
                Context.addFrame(Fields.DurationNano, (timestampEndNano - timestampStartNano) / 1000000);
                if (responseWriter != null) {
                    responseWriter.log(Context.peek());
                }
                Context.pop();
            }
        } else {
            try {
                return invocation.proceed();
            } catch (final Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.handleException(e);
                    return null;
                } else {
                    throw e;
                }
            }
        }
    }
}