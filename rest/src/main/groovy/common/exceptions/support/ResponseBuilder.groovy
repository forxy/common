package common.exceptions.support

import common.pojo.StatusEntity

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static javax.ws.rs.core.Response.Status

/**
 * Builder for error messages in the jax-rs response.
 */
final class ResponseBuilder {

    private ResponseBuilder() {
    }

    static Response build(final Status status, final String message) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.statusCode), message)
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    static Response build(final Status status, final List<String> messages) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.statusCode), messages)
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    static Response build(final Status status, final Throwable cause) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.statusCode), cause)
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    static Response build(final Status status, final String code, final String message) {
        final StatusEntity errorEntity = new StatusEntity(code, message)
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build()
    }
}
