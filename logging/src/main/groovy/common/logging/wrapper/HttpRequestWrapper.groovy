package common.logging.wrapper

import common.utils.EncodingHelper
import org.apache.commons.io.IOUtils

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import java.nio.charset.Charset

class HttpRequestWrapper extends HttpServletRequestWrapper {
    BufferedReader reader

    byte[] requestBody
    final ServletInputStream inputStream

    HttpRequestWrapper(final HttpServletRequest req) throws IOException {
        super(req)
        requestBody = IOUtils.toByteArray(req.getInputStream())

        inputStream = new ServletInputStream() {
            final InputStream is = new ByteArrayInputStream(requestBody)

            @Override
            int read() throws IOException {
                return is.read()
            }
        }
    }

    @Override
    BufferedReader getReader() throws IOException {
        if (reader == null) {
            final Charset charset = EncodingHelper.getCharsetByAlias(getCharacterEncoding())
            reader = new BufferedReader(new InputStreamReader(getInputStream(), charset))
        }
        return reader
    }
}