package common.logging.writer

import common.support.Context

/**
 * Can log the ContextData
 */
interface ILogWriter {

    void log(final Context.ContextData data)
}
