package common.rest.client

import common.rest.AbstractService
import common.rest.SystemStatusController
import common.status.ISystemStatusService
import common.status.api.StatusType
import common.status.api.SystemStatus
import spock.lang.Specification
import spock.lang.Subject

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * System status controller specification. But tests are generally for AbstractService logic testing
 */
@Subject([SystemStatusController, AbstractService])
class SystemStatusControllerSpec extends Specification {

    def "Service controller should return a valid JAX-RS response"() {
        given:
        SystemStatusController controller = new SystemStatusController(
                systemStatusService: Mock(ISystemStatusService) {
                    getStatus() >> { new SystemStatus(status: StatusType.GREEN) }
                }
        )
        when:
        Response response = controller.getSystemStatus(_ as UriInfo, _ as HttpHeaders);
        then:
        response
    }
}
