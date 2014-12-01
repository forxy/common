package common.rest.client.transport

import common.exceptions.ClientException

interface ITransport {

    class Response<R, E> {
        final R resource
        final E error
        String response
        Map<String, String> responseHeaders

        int httpStatusCode
        String httpStatusReason

        Response(final R resource, final E error, final Map<String, String> responseHeaders) {
            this.resource = resource
            this.error = error
            this.responseHeaders = responseHeaders
        }
    }

    interface IResponseHandler<R, E> {
        Response<R, E> handle(int statusCode, String statusReason, Map<String, String> responseHeaders,
                              InputStream responseStream) throws IOException
    }

    public <R, E> Response<R, E> performGet(String url, Map<String, String> headers,
                                            IResponseHandler<R, E> responseHandler) throws ClientException

    public <R, E> Response<R, E> performPost(String url, Map<String, String> headers, String entity,
                                             IResponseHandler<R, E> responseHandler) throws ClientException

    public <R, E> Response<R, E> performPut(String url, Map<String, String> headers, String entity,
                                            IResponseHandler<R, E> responseHandler) throws ClientException

    public <R, E> Response<R, E> performDelete(String url, Map<String, String> headers,
                                               IResponseHandler<R, E> responseHandler) throws ClientException
}