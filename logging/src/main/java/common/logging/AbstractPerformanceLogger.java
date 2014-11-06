package common.logging;

import common.logging.extractor.IHttpFieldExtractor;
import common.logging.support.Fields;
import common.logging.support.IExceptionHandler;
import common.logging.support.MetadataHelper;
import common.logging.writer.ILogWriter;
import org.springframework.beans.factory.InitializingBean;
import common.support.Configuration;
import common.support.Context;

import java.util.List;

/**
 * Base performance logger
 */
public abstract class AbstractPerformanceLogger implements InitializingBean {

    public static enum Configs {
        IsPayloadLoggingEnabled,
        IsPerformanceLoggingEnabled
    }

    protected Configuration configuration;

    protected IExceptionHandler exceptionHandler;

    protected ILogWriter requestWriter;

    protected ILogWriter responseWriter;

    protected ILogWriter requestPayloadWriter;

    protected ILogWriter responsePayloadWriter;

    protected List<IHttpFieldExtractor> requestFieldExtractors;

    protected List<IHttpFieldExtractor> responseFieldExtractors;

    protected String activityName = null;
    protected boolean isPayloadLoggingEnabled = false;
    protected boolean isPerformanceLoggingEnabled = false;

    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    public void setExceptionHandler(final IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setRequestWriter(final ILogWriter requestWriter) {
        this.requestWriter = requestWriter;
    }

    public void setResponseWriter(final ILogWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public void setRequestPayloadWriter(final ILogWriter requestPayloadWriter) {
        this.requestPayloadWriter = requestPayloadWriter;
    }

    public void setResponsePayloadWriter(final ILogWriter responsePayloadWriter) {
        this.responsePayloadWriter = responsePayloadWriter;
    }

    public void setRequestFieldExtractors(final List<IHttpFieldExtractor> requestFieldExtractors) {
        this.requestFieldExtractors = requestFieldExtractors;
    }

    public void setResponseFieldExtractors(final List<IHttpFieldExtractor> responseFieldExtractors) {
        this.responseFieldExtractors = responseFieldExtractors;
    }

    protected static void processException(final Throwable t) {
        if (!Context.contains(Fields.StatusCode)) {
            Context.addFrame(Fields.StatusCode, MetadataHelper.getShortErrorDescription(t));
        }
    }

    protected static void writeFrame(final ILogWriter writer) {
        if (writer != null) {
            writer.log(Context.peek());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (configuration != null) {
            isPayloadLoggingEnabled = configuration.getBoolean(Configs.IsPayloadLoggingEnabled);
            isPerformanceLoggingEnabled = configuration.getBoolean(Configs.IsPerformanceLoggingEnabled);
            activityName = configuration.get(Fields.ActivityName);
        }
    }
}
