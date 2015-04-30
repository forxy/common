package common.logging.filter

import common.utils.support.ContextData

/**
 * Filter based on Spring expression language
 */
class SpringELFilter implements IFilter {

    @Override
    ContextData doFilter(ContextData data) {
        return data
    }
}
