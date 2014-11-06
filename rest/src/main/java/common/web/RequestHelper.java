package common.web;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contains basic http parameters and headers processing
 */
public final class RequestHelper {

    private static final Pattern GUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    private RequestHelper() {
    }

    /**
     * Enumeration describing the most common request/response headers (and request
     * query parameters) used in REST-based services.
     */
    public enum Param {
        AUTHORIZATION("Authorization", "Authorization", "authorization"),
        TRANSACTION_GUID("TransactionGUID", "Transaction-GUID", "transaction_guid"),
        MESSAGE_GUID("MessageGUID", "Message-GUID", "message_guid"),
        SERVICE_VERSION("ServiceVersion", "Service-Version", "service_version"),
        RESPONDER_ID("ResponderID", "Responder-ID", "responder_id"),
        CLIENT_ID("ClientID", "Client-ID", "client_id");

        private final String name;
        private final String httpHeaderName;
        private final String queryParamName;

        private Param(final String name, final String httpHeaderName, final String queryParamName) {
            this.name = name;
            this.httpHeaderName = httpHeaderName;
            this.queryParamName = queryParamName;
        }

        public String getName() {
            return name;
        }

        public String getHttpHeaderName() {
            return httpHeaderName;
        }

        public String getQueryParamName() {
            return queryParamName;
        }
    }

    /**
     * Retrieves the request parameter (either query parameter or HTTP header) from the infrastructure
     * objects provided by the JAX-RS framework
     *
     * @param param   Parameter to be retrieved from URI Info (as query parameter) or HTTP headers
     * @param uriInfo URI Info provided by the JAX-RS framework
     * @param headers HTTP Headers provided by the JAX-RS framework
     * @return Query parameter (if specified), HTTP header (if specified), <code>null</tt> otherwise
     */
    public static String getRequestValue(final Param param, final UriInfo uriInfo, final HttpHeaders headers) {
        return getRequestValue(param, uriInfo, headers, null);
    }

    /**
     * Retrieves the request parameter (either query parameter or HTTP header) from the infrastructure
     * objects provided by the JAX-RS framework
     *
     * @param param        Parameter to be retrieved from URI Info (as query parameter) or HTTP headers
     * @param uriInfo      URI Info provided by the JAX-RS framework
     * @param headers      HTTP Headers provided by the JAX-RS framework
     * @param defaultValue The default value to be returned when neither query parameter nor HTTP
     *                     header is provided in the request
     * @return Query parameter (if specified), HTTP header (if specified), default value otherwise
     */
    public static String getRequestValue(final Param param, final UriInfo uriInfo, final HttpHeaders headers,
                                         final String defaultValue) {
        String value = null;
        if (uriInfo != null && uriInfo.getQueryParameters() != null) {
            value = uriInfo.getQueryParameters().getFirst(param.getQueryParamName());
        }
        if (value == null) {
            final List<String> headerValues = headers.getRequestHeader(param.getHttpHeaderName());
            if (headerValues != null && headerValues.size() > 0) {
                value = headerValues.get(0);
            }
        }
        return value == null || "".equals(value) ? defaultValue : value;
    }

    public static void checkIfGUID(final List<String> messages, final Param param, final String paramValue,
                                   final boolean allowEmpty) {
        if ((paramValue == null || "".equals(paramValue)) && !allowEmpty) {
            messages.add(param.getName() + " should be provided in the query string (" + param.getQueryParamName() +
                    ") or as HTTP header (" + param.getHttpHeaderName() + ")");
        }
        if (paramValue != null && !"".equals(paramValue) && !GUID_PATTERN.matcher(paramValue).matches()) {
            messages.add(param.getName() + "='" + paramValue + "' does not match " + GUID_PATTERN + " pattern");
        }
    }
}
