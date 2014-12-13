package common.logging.writer

import common.utils.support.Context

/**
 * Can log the ContextData
 */
interface ILogWriter {

    void log(final Context.ContextData data)
}
