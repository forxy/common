package common.rest.client;

import common.rest.client.transport.DefaultResponseHandler;
import common.rest.client.transport.ITransport;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import common.exceptions.ClientException;
import common.exceptions.HttpEventLogId;
import common.pojo.StatusEntity;
import common.utils.ValidationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * An abstract class serving as the base for REST Service Client implementations, providing
 * some basic functionality, such as JSON marshalling etc.
 */
public abstract class RestServiceClientSupport {
    protected static final String TRANSACTION_GUID = "Transaction-GUID";
    protected static final String MESSAGE_GUID = "Message-GUID";
    protected static final String CLIENT_ID = "Client-ID";
    protected static final String JSON_CONTENT_TYPE = "application/json";

    protected ObjectMapper mapper;

    protected ITransport transport;

    protected <R> ITransport.IResponseHandler<R, StatusEntity> createResponseHandler(
            final Class<R> resourceType) {
        return new DefaultResponseHandler<R>(mapper, resourceType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <C extends Collection<R>, R> ITransport.IResponseHandler<C, StatusEntity>
    createResponseCollectionHandler(final Class<C> collectionType, final Class<R> resourceType) {
        final CollectionType responseType =
                TypeFactory.defaultInstance().constructCollectionType(collectionType, resourceType);
        return new DefaultResponseHandler(mapper, responseType);
    }

    /**
     * Marshals provided object to the JSON format
     *
     * @param obj Object to be marshalled
     * @return String with the JSON string
     * @throws ClientException if an error occurs during marshalling
     */
    protected <T> String marshal(final T obj) throws ClientException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (final IOException e) {
            throw new ClientException(null, e, HttpEventLogId.UnexpectedException, e.getMessage());
        }
    }

    /**
     * Marshals provided object to the JSON format
     *
     * @param json  JSON source to be unmarshalled
     * @param clazz Expected type of the target (i.e. unmarshalled) object
     * @return An unmarshalled object instance
     * @throws ClientException if an error occurs during marshalling
     */
    protected <T> T unmarshal(final InputStream json, final Class<T> clazz) throws ClientException {
        try {
            return mapper.readValue(new InputStreamReader(json), clazz);
        } catch (final IOException e) {
            throw new ClientException(null, e, HttpEventLogId.UnexpectedException, e.getMessage());
        }
    }

    /**
     * Provides basic functionality to check error response for some predefined errors such as:
     * <ul>
     * <li>400 - <code>HttpStatus.SC_BAD_REQUEST</code>
     * <li>401 - <code>HttpStatus.SC_UNAUTHORIZED</code>
     * <li>403 - <code>HttpStatus.SC_FORBIDDEN</code>
     * <li>404 - <code>HttpStatus.SC_NOT_FOUND</code>
     * </ul>
     *
     * @param response Response from the server (containing either an entity or the error)
     * @return Response entity if there were no errors while communicating with the service
     * @throws ClientException if there was an error while communicating with the service
     */
    protected <T> T checkForError(final ITransport.Response<T, StatusEntity> response) throws ClientException {
        final StatusEntity error = response.getError();
        if (error != null) {
            if (String.valueOf(HttpStatus.SC_FORBIDDEN).equals(error.getCode())) {
                throw new ClientException(error, HttpEventLogId.AccessDenied);
            } else if (String.valueOf(HttpStatus.SC_UNAUTHORIZED).equals(error.getCode())) {
                throw new ClientException(error, HttpEventLogId.Unauthorized);
            } else if (String.valueOf(HttpStatus.SC_NOT_FOUND).equals(error.getCode())) {
                throw new ClientException(error, HttpEventLogId.ResourceNotFound);
            } else if (String.valueOf(HttpStatus.SC_BAD_REQUEST).equals(error.getCode())) {
                throw new ClientException(error, HttpEventLogId.InvalidClientInput);
            }
            throw processErrorResponse(error);
        } else {
            if (response.getResource() == null && response.getHttpStatusCode() != HttpStatus.SC_NO_CONTENT) {
                throw new ClientException(null, HttpEventLogId.UnexpectedException, "Response object is not " +
                        "initialized when instance with payload data is expected.");
            }
            return response.getResource();
        }
    }

    /**
     * Clients should overwrite this to return any custom exception that is to be thrown from the client.
     *
     * @param error Error response retrieved from the service
     * @return Client exception that is to be thrown
     */
    protected ClientException processErrorResponse(final StatusEntity error) {
        return new ClientException(error, HttpEventLogId.UnexpectedException,
                error != null ? "Error code: " + error.getCode() : "N/A");
    }

    /**
     * Validate a named parameter is in GUID format
     *
     * @param name       Parameter name
     * @param value      Parameter value
     * @param allowEmpty Specifies whether an empty value is considered as valid
     * @throws ClientException if parameter value is invalid
     */
    protected void validateGUID(final String name, final String value, final boolean allowEmpty)
            throws ClientException {
        if (StringUtils.isBlank(value) && !allowEmpty) {
            throw new ClientException(null, HttpEventLogId.InvalidClientInput,
                    "Parameter '" + name + "' should not be empty");
        }
        if (!StringUtils.isBlank(value) && !ValidationUtils.isValidGUID(value)) {
            throw new ClientException(null, HttpEventLogId.InvalidClientInput,
                    "Parameter '" + name + "' is not in GUID format");
        }
    }

    public void setTransport(ITransport transport) {
        this.transport = transport;
    }
}
