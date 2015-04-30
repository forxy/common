package common.logging.filter

import common.utils.support.ContextData

/**
 * Filter interface to exclude some sensitive data from writing
 */
interface IFilter {

    ContextData doFilter(ContextData data)
}
