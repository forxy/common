package common.logging.writer

import common.logging.support.LogHelper
import common.support.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Slf4j-based log writer implementation
 */
class LogWriter implements ILogWriter {

    private Logger logger

    LogWriter() {
        logger = LoggerFactory.getLogger(LogWriter.class)
    }

    LogWriter(final String name) {
        logger = LoggerFactory.getLogger(name)
    }

    @Override
    void log(final Context.ContextData data) {
        logger.info(LogHelper.contextDataToLogString(data))
    }
}
