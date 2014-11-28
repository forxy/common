/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package common.exceptions.support;

import common.exceptions.HttpEvent;
import common.exceptions.ServiceException;
import common.exceptions.ValidationException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This is the provider class which handles the {@link RuntimeException} and generates the response as per the details
 * available in the exception.
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(RuntimeException re) {
        ServiceException ex;
        Response response;
        if (re instanceof ValidationException) {
            ex = (ValidationException) re;
            response = ResponseBuilder.build(
                    Response.Status.fromStatusCode(ex.getEventLogID().getResponseID()),
                    ((ValidationException) re).getMessages());
        } else if (re instanceof ServiceException) {
            ex = (ServiceException) re;
            response = ResponseBuilder.build(
                    Response.Status.fromStatusCode(ex.getEventLogID().getResponseID()),
                    String.valueOf(ex.getEventLogID().getEventID()), ex.getMessage());
        } else {
            ex = new ServiceException(re,
                    HttpEvent.UnexpectedException,
                    ExceptionUtils.getRootCauseMessage(re));
            response = ResponseBuilder.build(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    String.valueOf(ex.getStatusCode()),
                    ExceptionUtils.getFullStackTrace(re));
        }

        if (re instanceof WebApplicationException) {
            if (re.getCause() instanceof ServiceException) {
                ex = (ServiceException) re.getCause();
                response = ResponseBuilder.build(
                        Response.Status.INTERNAL_SERVER_ERROR,
                        String.valueOf(ex.getEventLogID().getEventID()),
                        ex.getMessage());
            } else {
                ex = new ServiceException(re,
                        HttpEvent.UnexpectedException,
                        ExceptionUtils.getRootCauseMessage(re));
                response = ResponseBuilder.build(
                        Response.Status.fromStatusCode(((WebApplicationException) re).getResponse().getStatus()),
                        String.valueOf(((WebApplicationException) re).getResponse().getStatus()),
                        ExceptionUtils.getFullStackTrace(re));
            }
        }
        ex.log(LOGGER);
        return response;
    }
}
