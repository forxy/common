package common.exceptions.support

import common.api.StatusEntity
import common.exceptions.EventLogBase

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Builder for error messages in the jax-rs response.
 */
final class ResponseBuilder {

    private ResponseBuilder() {
    }

    static Response build(final EventLogBase event, final String message) {
        final StatusEntity errorEntity = new StatusEntity(event, message)
        return Response
                .status(event.httpCode)
                .entity(errorEntity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }

    static Response build(final EventLogBase event, final List<String> messages) {
        final StatusEntity errorEntity = new StatusEntity(event, messages)
        return Response
                .status(event.httpCode)
                .entity(errorEntity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }

    static Response build(final EventLogBase event, final Throwable cause) {
        final StatusEntity errorEntity = new StatusEntity(event, cause)
        return Response
                .status(event.httpCode)
                .entity(errorEntity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }

    static Response build(final EventLogBase event, final Integer code, final String message) {
        final StatusEntity errorEntity = new StatusEntity(event, message)
        return Response
                .status(code)
                .entity(errorEntity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }
}
