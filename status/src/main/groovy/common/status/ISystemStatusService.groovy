package common.status

import common.status.pojo.SystemStatus

/**
 * Facade includes the business logic for service and its components health check
 */
interface ISystemStatusService {

    SystemStatus getStatus()
}
