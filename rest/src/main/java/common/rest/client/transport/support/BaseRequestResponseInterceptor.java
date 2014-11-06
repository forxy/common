package common.rest.client.transport.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HttpContext;

public abstract class BaseRequestResponseInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor
{
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException,
            IOException
    {
        byte[] payload = null;
        if (request instanceof HttpEntityEnclosingRequest)
        {
            final HttpEntityEnclosingRequest eer = (HttpEntityEnclosingRequest) request;
            payload = IOUtils.toByteArray(eer.getEntity().getContent());
        }

        final RequestLine requestLine = request.getRequestLine();
        processRequest(requestLine.getUri(), request.getRequestLine().getMethod(),
                getHeaderMap(request.getAllHeaders()), payload);
    }

    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException,
            IOException
    {
        byte[] payload = null;
        if (response.getEntity() != null)
        {
            payload = IOUtils.toByteArray(response.getEntity().getContent());
        }

        final StatusLine status = response.getStatusLine();
        processResponse(status.getStatusCode(), status.getReasonPhrase(),
                getHeaderMap(response.getAllHeaders()), payload);
    }

    private static Map<String, List<String>> getHeaderMap(final Header[] headers)
    {
        Map<String, List<String>> headerMap = null;
        if (headers != null && headers.length > 0)
        {
            headerMap = new LinkedHashMap<String, List<String>>();
            for (final Header h : headers)
            {
                List<String> l = headerMap.get(h.getName());
                if (l == null)
                {
                    l = new ArrayList<String>();
                    headerMap.put(h.getName(), l);
                }
                l.add(h.getValue());
            }
        }
        return headerMap;
    }

    protected void processRequest(final String url, final String method,
            final Map<String, List<String>> headers, final byte[] payload)
    {
    }

    protected void processResponse(final int statusCode, final String reasonPhrase,
            final Map<String, List<String>> headers, final byte[] payload)
    {
    }
}
