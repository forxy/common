package common.rest

import common.support.SystemProperties
import common.web.RequestHelper as RH

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

import static javax.ws.rs.core.Response.ResponseBuilder as RB
import static javax.ws.rs.core.Response.Status

/**
 * Base class for rest service endpoint implementation
 */
public abstract class AbstractService {

    protected static final String TRANSACTION_GUID = RH.Param.TRANSACTION_GUID.httpHeaderName;
    protected static final String MESSAGE_GUID = RH.Param.MESSAGE_GUID.httpHeaderName;
    protected static final String SERVICE_VERSION = RH.Param.SERVICE_VERSION.httpHeaderName;
    protected static final String RESPONDER_ID = RH.Param.RESPONDER_ID.httpHeaderName;
    protected static final String CLIENT_ID = RH.Param.SERVICE_VERSION.httpHeaderName;

    protected static String getTransactionGUID(final HttpHeaders headers, final UriInfo uriInfo) {
        return RH.getRequestValue(RH.Param.TRANSACTION_GUID, uriInfo, headers, UUID.randomUUID() as String);
    }

    protected static String getMessageGUID(final HttpHeaders headers, final UriInfo uriInfo) {
        return RH.getRequestValue(RH.Param.MESSAGE_GUID, uriInfo, headers);
    }

    /**
     * Creates the <tt>successful</tt> response builder with the response entity and pre-populated
     * response headers (see {@link #populateHeaders} method).
     *
     * @param response Response entity
     * @param uriInfo URI Info provided by the JAX-RS infrastructure
     * @param headers HTTP Headers provided by the JAX-RS infrastructure
     * @return Response builder created
     */
    protected static <RS> RB respondWith(final RS response, final UriInfo uriInfo, final HttpHeaders headers) {
        final RB builder = Response.ok(response);
        return populateHeaders(builder, null, uriInfo, headers);
    }

    /**
     * Creates the response builder with the provided response status and pre-populated
     * response headers (see {@link #populateHeaders} method).
     *
     * @param status Response status
     * @param uriInfo URI Info provided by the JAX-RS infrastructure
     * @param headers HTTP Headers provided by the JAX-RS infrastructure
     * @return Response builder created
     */
    protected static RB respondWith(final Status status, final UriInfo uriInfo, final HttpHeaders headers) {
        final RB builder = Response.status(status);
        return populateHeaders(builder, null, uriInfo, headers);
    }

    /**
     * Creates the <tt>successful</tt> response builder with the response entity and pre-populated
     * response headers (see {@link #populateHeaders} method).
     *
     * @param response Response entity
     * @param transactionGUID Transaction GUID
     * @param uriInfo URI Info provided by the JAX-RS infrastructure
     * @param headers HTTP Headers provided by the JAX-RS infrastructure
     * @return Response builder created
     */
    protected static <RS> RB respondWith(final RS response, final String transactionGUID, final UriInfo uriInfo,
                                         final HttpHeaders headers) {
        final RB builder = Response.ok(response);
        return populateHeaders(builder, transactionGUID, uriInfo, headers);
    }

    /**
     * Creates the response builder with the provided response status and pre-populated
     * response headers (see {@link #populateHeaders} method).
     *
     * @param status Response status
     * @param transactionGUID Transaction GUID
     * @param uriInfo URI Info provided by the JAX-RS infrastructure
     * @param headers HTTP Headers provided by the JAX-RS infrastructure
     * @return Response builder created
     */
    protected static RB respondWith(final Status status, final String transactionGUID, final UriInfo uriInfo,
                                    final HttpHeaders headers) {
        final RB builder = Response.status(status);
        return populateHeaders(builder, transactionGUID, uriInfo, headers);
    }

    /**
     * Populates the following response headers:
     * <ul>
     * <li>Transaction-GUID</li>
     * <li>Message-GUID</li>
     * <li>Service-Version</li>
     * <li>Responder-ID</li>
     * </ul>
     *
     * @param builder Response builder, whose headers are to be populated
     * @param transactionGUID Transaction GUID
     * @param uriInfo URI Info provided by the JAX-RS infrastructure
     * @param headers HTTP Headers provided by the JAX-RS infrastructure
     * @return Response builder
     */
    protected static RB populateHeaders(RB builder, final String transactionGUID,
                                        final UriInfo uriInfo, final HttpHeaders headers) {
        if (transactionGUID) {
            builder.header(TRANSACTION_GUID, transactionGUID);
        } else {
            String tGUID = getTransactionGUID(headers, uriInfo);
            if (tGUID) {
                builder.header(TRANSACTION_GUID, tGUID);
            }
        }
        final String messageGUID = getMessageGUID(headers, uriInfo);
        if (messageGUID) {
            builder = builder.header(MESSAGE_GUID, messageGUID);
        }
        builder.header(SERVICE_VERSION, SystemProperties.serviceVersion);
        builder.header(RESPONDER_ID, SystemProperties.machineID);
        return builder;
    }
}
