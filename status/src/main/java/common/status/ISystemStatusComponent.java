package common.status;

import common.status.pojo.ComponentStatus;

/**
 * Implementing this interface you should return health status of the implementor
 */
public interface ISystemStatusComponent {

    ComponentStatus getStatus();
}
