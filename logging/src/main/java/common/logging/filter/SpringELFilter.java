package common.logging.filter;

import common.support.Context;

/**
 * Filter based on Spring expression language
 */
public class SpringELFilter implements IFilter {

    @Override
    public Context.ContextData doFilter(Context.ContextData data) {
        return data;
    }
}
