package common.logging.filter

import common.utils.support.Context

/**
 * Filter interface to exclude some sensitive data from writing
 */
interface IFilter {

    Context.ContextData doFilter(Context.ContextData data)
}
