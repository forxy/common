package common.rest.client.transport.support

import org.apache.http.HttpEntity
import org.apache.http.HttpException
import org.apache.http.HttpResponse
import org.apache.http.HttpResponseInterceptor
import org.apache.http.entity.BufferedHttpEntity
import org.apache.http.protocol.HttpContext

/**
 * Creates the <b>repeatable</b> <code>HttpResponse</code>'s {@link org.apache.http.HttpEntity}
 * allowing multiple invocations of <code>response.getEntity().getContent()</code>
 */
class RepeatableEntityCreatingResponseInterceptor implements HttpResponseInterceptor {

    @Override
    void process(final HttpResponse response, final HttpContext context) throws HttpException,
            IOException {
        final HttpEntity entity = response.entity
        if (entity && !entity.repeatable) {
            response.entity = new BufferedHttpEntity(entity)
        }
    }
}
