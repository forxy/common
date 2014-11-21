package common.logging.filter

import common.support.Context

/**
 * Filter based on Spring expression language
 */
class SpringELFilter implements IFilter {

    @Override
    Context.ContextData doFilter(Context.ContextData data) {
        return data
    }
}
