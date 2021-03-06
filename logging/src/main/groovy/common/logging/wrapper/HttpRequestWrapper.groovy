package common.logging.wrapper

import common.utils.EncodingHelper
import org.apache.commons.io.IOUtils

import javax.servlet.ReadListener
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
        requestBody = IOUtils.toByteArray req.inputStream

        inputStream = new ServletInputStream() {
            final InputStream is = new ByteArrayInputStream(requestBody)

            @Override
            int read() throws IOException {
                return is.read()
            }

            @Override
            boolean isFinished() {
                return !is.available()
            }

            @Override
            boolean isReady() {
                return is != null
            }

            @Override
            void setReadListener(ReadListener readListener) {
            }
        }
    }

    @Override
    BufferedReader getReader() throws IOException {
        if (!reader) {
            final Charset charset = EncodingHelper.getCharsetByAlias(characterEncoding)
            reader = new BufferedReader(new InputStreamReader(inputStream, charset))
        }
        return reader
    }
}