package common.rest.client.transport.support

import org.apache.http.*
import org.apache.http.protocol.HttpContext

import static org.apache.commons.io.IOUtils.toByteArray

abstract class BaseRequestResponseInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {

    @Override
    void process(final HttpRequest request, final HttpContext context) throws HttpException,
            IOException {
        byte[] payload = null
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntityEnclosingRequest eer = (HttpEntityEnclosingRequest) request
            payload = toByteArray(eer.entity.content)
        }

        final RequestLine requestLine = request.requestLine
        processRequest(requestLine.uri, request.requestLine.method, getHeaderMap(request.allHeaders), payload)
    }

    @Override
    void process(final HttpResponse response, final HttpContext context) throws HttpException,
            IOException {
        byte[] payload = null
        if (response.entity) {
            payload = toByteArray(response.entity.content)
        }

        final StatusLine status = response.statusLine
        processResponse(status.statusCode, status.reasonPhrase, getHeaderMap(response.allHeaders), payload)
    }

    private static Map<String, List<String>> getHeaderMap(final Header[] headers) {
        Map<String, List<String>> headerMap = null
        if (headers) {
            headerMap = [:]
            headers.each {
                if (!headerMap[it.name]) {
                    headerMap[it.name] = [it.value]
                } else {
                    headerMap[it.name] << it.value
                }
            }
        }
        return headerMap
    }

    protected void processRequest(final String url, final String method,
                                  final Map<String, List<String>> headers, final byte[] payload) {
    }

    protected void processResponse(final int statusCode, final String reasonPhrase,
                                   final Map<String, List<String>> headers, final byte[] payload) {
    }
}
