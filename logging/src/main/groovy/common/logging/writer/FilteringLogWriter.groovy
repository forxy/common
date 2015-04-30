package common.logging.writer

import common.logging.filter.IFilter
import common.utils.support.ContextData

/**
 * Writer applies configured filters before logging data
 */
class FilteringLogWriter extends LogWriter {

    List<IFilter> filters

    @Override
    void log(final ContextData data) {
        ContextData frame = data
        filters.each { frame = it.doFilter(frame) }
        super.log(frame)
    }
}
