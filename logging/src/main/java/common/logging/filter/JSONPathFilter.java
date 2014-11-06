package common.logging.filter;

import common.support.Context;

/**
 * Filter based on JSON path
 */
public class JSONPathFilter implements IFilter {

    @Override
    public Context.ContextData doFilter(Context.ContextData data) {
        return data;
    }
}
