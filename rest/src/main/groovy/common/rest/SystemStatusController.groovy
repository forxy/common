package common.rest

import common.status.ISystemStatusService

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * System status controller
 * You can call it to get service health status
 */
@Path('/status')
@Produces(MediaType.APPLICATION_JSON)
class SystemStatusController extends AbstractService {

    ISystemStatusService systemStatusService

    @GET
    Response getSystemStatus(@Context final UriInfo uriInfo, @Context final HttpHeaders headers) {
        return respondWith(systemStatusService.status, uriInfo, headers).build()
    }
}
