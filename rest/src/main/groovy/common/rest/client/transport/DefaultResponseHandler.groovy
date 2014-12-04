/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package common.rest.client.transport

import common.api.StatusEntity
import org.apache.commons.io.IOUtils
import org.apache.http.HttpStatus
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.type.CollectionType

class DefaultResponseHandler<R> implements ITransport.IResponseHandler<R, StatusEntity> {

    private final ObjectMapper mapper

    private final CollectionType collectionType

    private final Class<R> resourceType

    DefaultResponseHandler(final ObjectMapper mapper, final Class<R> resourceType) {
        this.mapper = mapper
        collectionType = null
        this.resourceType = resourceType
    }

    DefaultResponseHandler(final ObjectMapper mapper, final CollectionType collectionType) {
        this.mapper = mapper
        this.collectionType = collectionType
        resourceType = null
    }

    @Override
    ITransport.Response<R, StatusEntity> handle(final int statusCode, final String statusReason,
                                                final Map<String, String> responseHeaders,
                                                final InputStream responseStream) throws IOException {
        final byte[] responseBytes = responseStream ? IOUtils.toByteArray(responseStream) : null
        String response = responseBytes ? new String(responseBytes, 'UTF-8') : null
        ITransport.Response<R, StatusEntity> result

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            final R resource = collectionType ?
                    (R) mapper.readValue(responseBytes, collectionType) :
                    mapper.readValue(responseBytes, resourceType)
            result = new ITransport.Response<R, StatusEntity>(resource, null, responseHeaders)
            result.response = response
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            result = new ITransport.Response<R, StatusEntity>(null, null, responseHeaders)
        } else {
            final StatusEntity error = new StatusEntity(statusCode, response)
            result = new ITransport.Response<R, StatusEntity>(null, error, null)
        }
        result.httpStatusCode = statusCode
        result.httpStatusReason = statusReason
        return result
    }
}
