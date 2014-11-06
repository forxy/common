package common.rest.client.transport;

import common.exceptions.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ITransport {

    class Response<R, E> {
        private final R resource;
        private final E error;
        private String response;
        private Map<String, String> responseHeaders;

        private int httpStatusCode;
        private String httpStatusReason;

        public Response(final R resource, final E error, final Map<String, String> responseHeaders) {
            this.resource = resource;
            this.error = error;
            this.responseHeaders = responseHeaders;
        }

        public R getResource() {
            return resource;
        }

        public E getError() {
            return error;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(final String response) {
            this.response = response;
        }

        public Map<String, String> getResponseHeaders() {
            return responseHeaders;
        }

        public void setResponseHeaders(final Map<String, String> responseHeaders) {
            this.responseHeaders = responseHeaders;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public void setHttpStatusCode(final int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public String getHttpStatusReason() {
            return httpStatusReason;
        }

        public void setHttpStatusReason(final String httpStatusReason) {
            this.httpStatusReason = httpStatusReason;
        }
    }

    interface IResponseHandler<R, E> {
        Response<R, E> handle(int statusCode, String statusReason, Map<String, String> responseHeaders,
                              InputStream responseStream) throws IOException;
    }

    <R, E> Response<R, E> performGet(String url, Map<String, String> headers,
                                     IResponseHandler<R, E> responseHandler) throws ClientException;

    <R, E> Response<R, E> performPost(String url, Map<String, String> headers, String entity,
                                      IResponseHandler<R, E> responseHandler) throws ClientException;

    <R, E> Response<R, E> performPut(String url, Map<String, String> headers, String entity,
                                     IResponseHandler<R, E> responseHandler) throws ClientException;

    <R, E> Response<R, E> performDelete(String url, Map<String, String> headers,
                                        IResponseHandler<R, E> responseHandler) throws ClientException;
}