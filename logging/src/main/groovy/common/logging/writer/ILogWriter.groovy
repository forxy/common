package common.logging.writer

import common.utils.support.ContextData

/**
 * Can log the ContextData
 */
interface ILogWriter {

    void log(final ContextData data)
}
