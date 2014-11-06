/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package common.rest.client.transport;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import common.pojo.StatusEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class DefaultResponseHandler<R> implements ITransport.IResponseHandler<R, StatusEntity> {
    private final ObjectMapper m_mapper;
    private final CollectionType m_collectionType;
    private final Class<R> m_resourceType;

    public DefaultResponseHandler(final ObjectMapper mapper, final Class<R> resourceType) {
        m_mapper = mapper;
        m_collectionType = null;
        m_resourceType = resourceType;
    }

    public <C extends Collection<R>> DefaultResponseHandler(final ObjectMapper mapper,
                                                            final CollectionType collectionType) {
        m_mapper = mapper;
        m_collectionType = collectionType;
        m_resourceType = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ITransport.Response<R, StatusEntity> handle(final int statusCode, final String statusReason,
                                                      final Map<String, String> responseHeaders, final InputStream responseStream) throws IOException {
        final byte[] responseBytes = responseStream != null ? IOUtils.toByteArray(responseStream) : null;
        String response = responseBytes != null ? new String(responseBytes, "UTF-8") : null;
        ITransport.Response<R, StatusEntity> result;

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            final R resource = (m_collectionType != null)
                    ? (R) m_mapper.readValue(responseBytes, m_collectionType)
                    : m_mapper.readValue(responseBytes, m_resourceType);
            result = new ITransport.Response<R, StatusEntity>(resource, null, responseHeaders);
            result.setResponse(response);
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            result = new ITransport.Response<R, StatusEntity>(null, null, responseHeaders);
        } else {
            final StatusEntity error =
                    new StatusEntity(Integer.toString(statusCode), response);
            result = new ITransport.Response<R, StatusEntity>(null, error, null);
        }
        result.setHttpStatusCode(statusCode);
        result.setHttpStatusReason(statusReason);
        return result;
    }
}
