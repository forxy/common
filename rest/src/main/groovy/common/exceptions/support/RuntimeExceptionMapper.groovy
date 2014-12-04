/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package common.exceptions.support

import common.exceptions.ServiceException
import common.exceptions.ValidationException
import org.apache.commons.lang.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

import static common.exceptions.HttpEvent.UnexpectedException
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import static javax.ws.rs.core.Response.Status.fromStatusCode

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
            response = ResponseBuilder.build(
                    fromStatusCode(ex.eventLogID.httpCode),
                    ((ValidationException) re).messages
            )
        } else if (re instanceof ServiceException) {
            ex = (ServiceException) re
            response = ResponseBuilder.build(
                    fromStatusCode(ex.eventLogID.httpCode),
                    ex.eventLogID.eventID,
                    ex.message
            )
        } else {
            ex = new ServiceException(re,
                    UnexpectedException,
                    ExceptionUtils.getRootCauseMessage(re)
            )
            response = ResponseBuilder.build(
                    INTERNAL_SERVER_ERROR,
                    ex.statusCode,
                    ExceptionUtils.getFullStackTrace(re)
            )
        }

        if (re instanceof WebApplicationException) {
            if (re.cause instanceof ServiceException) {
                ex = (ServiceException) re.cause
                response = ResponseBuilder.build(
                        INTERNAL_SERVER_ERROR,
                        ex.eventLogID.eventID,
                        ex.message
                )
            } else {
                ex = new ServiceException(re, UnexpectedException, ExceptionUtils.getRootCauseMessage(re))
                response = ResponseBuilder.build(
                        fromStatusCode(((WebApplicationException) re).response.status),
                        ((WebApplicationException) re).response.status,
                        ExceptionUtils.getFullStackTrace(re)
                )
            }
        }
        ex.log(LOGGER)
        return response
    }
}
