package common.logging.writer;

import common.logging.support.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.support.Context;

/**
 * Slf4j-based log writer implementation
 */
public class LogWriter implements ILogWriter {

    private Logger logger;

    public LogWriter() {
        logger = LoggerFactory.getLogger(LogWriter.class);
    }

    public LogWriter(final String name) {
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void log(final Context.ContextData data) {
        logger.info(LogHelper.contextDataToLogString(data));
    }
}
