package common.rest.client.transport.support;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;

/**
 * Creates the <b>repeatable</b> <code>HttpResponse</code>'s {@link org.apache.http.HttpEntity}
 * allowing multiple invocations of <code>response.getEntity().getContent()</code>
 */
public class RepeatableEntityCreatingResponseInterceptor implements HttpResponseInterceptor
{
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException,
            IOException
    {
        final HttpEntity entity = response.getEntity();
        if (entity != null && !entity.isRepeatable())
        {
            final HttpEntity proxy = new BufferedHttpEntity(entity);
            response.setEntity(proxy);
        }
    }
}
