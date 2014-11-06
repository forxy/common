package common.logging.writer;

import common.logging.filter.IFilter;
import common.support.Context;

import java.util.List;

/**
 * Writer applies configured filters before logging data
 */
public class FilteringLogWriter extends LogWriter {

    private List<IFilter> filters;

    @Override
    public void log(final Context.ContextData data) {
        Context.ContextData frame = data;
        for (IFilter filter : filters) {
            frame = filter.doFilter(frame);
        }
        super.log(frame);
    }

    public void setFilters(final List<IFilter> filters) {
        this.filters = filters;
    }
}
