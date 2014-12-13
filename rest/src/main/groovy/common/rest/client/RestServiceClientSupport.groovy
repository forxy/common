package common.rest.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.TypeFactory
import common.api.StatusEntity
import common.exceptions.ClientException
import common.exceptions.HttpEvent
import common.rest.client.transport.DefaultResponseHandler
import common.rest.client.transport.ITransport
import common.utils.ValidationUtils
import common.web.RequestHelper
import org.apache.http.HttpStatus

import static common.web.RequestHelper.Param.CLIENT_ID
import static common.web.RequestHelper.Param.TRANSACTION_GUID
import static javax.ws.rs.core.HttpHeaders.ACCEPT
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE

/**
 * An abstract class serving as the base for REST Service Client implementations, providing
 * some basic functionality, such as JSON marshalling etc.
 */
abstract class RestServiceClientSupport {
    protected static final String JSON_CONTENT_TYPE = 'application/json'

    ObjectMapper mapper
    ITransport transport
    String endpoint
    String clientID

    protected <R> ITransport.IResponseHandler<R, StatusEntity> createResponseHandler(
            final Class<R> resourceType) {
        return new DefaultResponseHandler<R>(mapper, resourceType)
    }

    protected <C extends Collection, R> ITransport.IResponseHandler<C, StatusEntity> createResponseCollectionHandler(
            final Class<C> collectionType, final Class<R> resourceType) {
        final CollectionType responseType =
                TypeFactory.defaultInstance().constructCollectionType(collectionType, resourceType)
        return new DefaultResponseHandler(mapper, responseType)
    }

    /**
     * Marshals provided object to the JSON format
     *
     * @param obj Object to be marshalled
     * @return String with the JSON string
     * @throws common.exceptions.ClientException if an error occurs during marshalling
     */
    protected <T> String marshal(final T obj) throws ClientException {
        try {
            return mapper.writeValueAsString(obj)
        } catch (final IOException e) {
            throw new ClientException(null, e, HttpEvent.UnexpectedException, e.message)
        }
    }

    /**
     * Marshals provided object to the JSON format
     *
     * @param json JSON source to be unmarshalled
     * @param clazz Expected type of the target (i.e. unmarshalled) object
     * @return An unmarshalled object instance
     * @throws common.exceptions.ClientException if an error occurs during marshalling
     */
    protected <T> T unmarshal(final InputStream json, final Class<T> clazz) throws ClientException {
        try {
            return mapper.readValue(new InputStreamReader(json), clazz)
        } catch (final IOException e) {
            throw new ClientException(null, e, HttpEvent.UnexpectedException, e.message)
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
     * @throws common.exceptions.ClientException if there was an error while communicating with the service
     */
    protected static <T> T checkForError(final ITransport.Response<T, StatusEntity> response) throws ClientException {
        final StatusEntity error = response.error
        if (error) {
            if (HttpStatus.SC_FORBIDDEN == response.httpStatusCode) {
                throw new ClientException(error, HttpEvent.AccessDenied)
            } else if (HttpStatus.SC_UNAUTHORIZED == response.httpStatusCode) {
                throw new ClientException(error, HttpEvent.Unauthorized)
            } else if (HttpStatus.SC_NOT_FOUND == response.httpStatusCode) {
                throw new ClientException(error, HttpEvent.ResourceNotFound)
            } else if (HttpStatus.SC_BAD_REQUEST == response.httpStatusCode) {
                throw new ClientException(error, HttpEvent.InvalidClientInput)
            }
            throw processErrorResponse(error)
        } else {
            if (!response.resource && response.httpStatusCode != HttpStatus.SC_NO_CONTENT) {
                throw new ClientException(null, HttpEvent.UnexpectedException,
                        'Response object is not initialized when instance with payload data is expected.')
            }
            return response.resource
        }
    }

    /**
     * Clients should overwrite this to return any custom exception that is to be thrown from the client.
     *
     * @param error Error response retrieved from the service
     * @return Client exception that is to be thrown
     */
    protected static ClientException processErrorResponse(final StatusEntity error) {
        return new ClientException(error, HttpEvent.UnexpectedException, error ? "Error code: $error.code" : 'N/A')
    }

    protected Map<String, String> buildHeaders(final String transactionGUID) throws ClientException {
        validateGUID(RequestHelper.Param.AUTHORIZATION.httpHeaderName, transactionGUID, true)

        return [
                (ACCEPT)                         : JSON_CONTENT_TYPE,
                (CONTENT_TYPE)                   : JSON_CONTENT_TYPE,
                (TRANSACTION_GUID.httpHeaderName): transactionGUID ?: UUID.randomUUID() as String,
                (CLIENT_ID.httpHeaderName)       : clientID
        ]
    }

    /**
     * Validate a named parameter is in GUID format
     *
     * @param name Parameter name
     * @param value Parameter value
     * @param allowEmpty Specifies whether an empty value is considered as valid
     * @throws common.exceptions.ClientException if parameter value is invalid
     */
    protected static void validateGUID(final String name, final String value, final boolean allowEmpty)
            throws ClientException {
        if (!value && !allowEmpty) {
            throw new ClientException(null, HttpEvent.InvalidClientInput,
                    "Parameter '$name' should not be empty" as String)
        } else if (!value && !ValidationUtils.isValidGUID(value)) {
            throw new ClientException(null, HttpEvent.InvalidClientInput,
                    "Parameter '$name' is not in GUID format" as String)
        }
    }
}
