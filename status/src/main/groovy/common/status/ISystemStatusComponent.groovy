package common.status

import common.status.api.ComponentStatus

/**
 * Implementing this interface you should return health status of the implementor
 */
interface ISystemStatusComponent {

    ComponentStatus getStatus()
}
