/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package common.exceptions.support

import common.exceptions.ServiceException
import common.exceptions.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

import static common.exceptions.HttpEvent.UnexpectedException

/**
 * This is the provider class which handles the {@link RuntimeException} and generates the response as per the details
 * available in the exception.
 */
@Provider
class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class)

    /**
     * {@inheritDoc}
     */
    @Override
    Response toResponse(RuntimeException re) {
        ServiceException ex
        Response response
        if (re instanceof ValidationException) {
            ex = (ValidationException) re
            response = ResponseBuilder.build(ex.eventLogID, ((ValidationException) re).messages)
        } else if (re instanceof ServiceException) {
            ex = (ServiceException) re
            response = ResponseBuilder.build(ex.eventLogID, ex.message)
        } else {
            ex = new ServiceException(re, UnexpectedException)
            response = ResponseBuilder.build(ex.eventLogID, ex.message)
        }

        if (re instanceof WebApplicationException) {
            if (re.cause instanceof ServiceException) {
                ex = (ServiceException) re.cause
                response = ResponseBuilder.build(ex.eventLogID, ex.message)
            } else {
                ex = new ServiceException(re, UnexpectedException)
                response = ResponseBuilder.build(
                        ex.eventLogID,
                        ((WebApplicationException) re).response.status, ex.message
                )
            }
        }
        ex.log(LOGGER)
        return response
    }
}
