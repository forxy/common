package common.status

import common.status.api.ComponentStatus
import common.status.api.StatusType
import common.status.api.SystemStatus
import common.utils.support.SystemProperties

/**
 * System status service implementation
 */
class SystemStatusService implements ISystemStatusService {

    List<ISystemStatusComponent> components

    @Override
    SystemStatus getStatus() {
        StatusType systemStatusType = null
        List<ComponentStatus> componentStatuses = []
        if (components) {
            componentStatuses = components*.status
        } else {
            systemStatusType = StatusType.RED
        }

        return new SystemStatus(
                SystemProperties.serviceName,
                SystemProperties.hostAddress,
                SystemProperties.serviceVersion,
                systemStatusType ?: getTheWorstStatus(componentStatuses),
                componentStatuses
        )
    }

    static StatusType getTheWorstStatus(final List<ComponentStatus> componentStatuses) {
        StatusType theWorstStatus = StatusType.GREEN
        componentStatuses.each {
            if (it.status.ordinal() > theWorstStatus.ordinal()) {
                theWorstStatus = it.status
            }
        }
        return theWorstStatus
    }
}
