package common.logging.writer;

import common.support.Context;

/**
 * Can log the ContextData
 */
public interface ILogWriter {

    void log(final Context.ContextData data);
}
