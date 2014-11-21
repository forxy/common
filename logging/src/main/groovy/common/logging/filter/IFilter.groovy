package common.logging.filter

import common.support.Context

/**
 * Filter interface to exclude some sensitive data from writing
 */
interface IFilter {

    Context.ContextData doFilter(Context.ContextData data)
}
