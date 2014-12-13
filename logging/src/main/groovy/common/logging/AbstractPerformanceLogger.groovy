package common.logging

import common.logging.extractor.IHttpFieldExtractor
import common.logging.support.IExceptionHandler
import common.logging.support.MetadataHelper
import common.logging.writer.ILogWriter
import common.utils.support.Configuration
import common.utils.support.Context
import org.springframework.beans.factory.InitializingBean

import static common.logging.AbstractPerformanceLogger.Configs.IsPayloadLoggingEnabled
import static common.logging.AbstractPerformanceLogger.Configs.IsPerformanceLoggingEnabled
import static common.logging.support.Fields.ActivityName
import static common.logging.support.Fields.StatusCode

/**
 * Base performance logger
 */
abstract class AbstractPerformanceLogger implements InitializingBean {

    static enum Configs {
        IsPayloadLoggingEnabled,
        IsPerformanceLoggingEnabled
    }

    Configuration configuration

    IExceptionHandler exceptionHandler

    ILogWriter requestWriter

    ILogWriter responseWriter

    ILogWriter requestPayloadWriter

    ILogWriter responsePayloadWriter

    List<IHttpFieldExtractor> requestFieldExtractors

    List<IHttpFieldExtractor> responseFieldExtractors

    String activityName = null
    boolean isPayloadLoggingEnabled = false
    boolean isPerformanceLoggingEnabled = false

    static void processException(final Throwable t) {
        if (!Context.contains(StatusCode)) {
            Context.addFrame(StatusCode, MetadataHelper.getShortErrorDescription(t))
        }
    }

    static void writeFrame(final ILogWriter writer) {
        writer?.log(Context.peek())
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (configuration != null) {
            isPayloadLoggingEnabled = configuration.getBoolean(IsPayloadLoggingEnabled)
            isPerformanceLoggingEnabled = configuration.getBoolean(IsPerformanceLoggingEnabled)
            activityName = configuration.get(ActivityName)
        }
    }
}
