package common.exceptions.support;

import common.pojo.StatusEntity;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status;

/**
 * Builder for error messages in the jax-rs response.
 */
public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static Response build(final Status status, final String message) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.getStatusCode()), message);
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static Response build(final Status status, final List<String> messages) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.getStatusCode()), messages);
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static Response build(final Status status, final Throwable cause) {
        final StatusEntity errorEntity = new StatusEntity(String.valueOf(status.getStatusCode()), cause);
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static Response build(final Status status, final String code, final String message) {
        final StatusEntity errorEntity = new StatusEntity(code, message);
        return Response.status(status).entity(errorEntity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
