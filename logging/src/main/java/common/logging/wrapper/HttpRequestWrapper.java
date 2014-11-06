package common.logging.wrapper;

import org.apache.commons.io.IOUtils;
import common.utils.EncodingHelper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class HttpRequestWrapper extends HttpServletRequestWrapper {
    private BufferedReader br;

    private byte[] requestBody;
    private final ServletInputStream sis;

    public HttpRequestWrapper(final HttpServletRequest req) throws IOException {
        super(req);
        requestBody = IOUtils.toByteArray(req.getInputStream());

        sis = new ServletInputStream() {
            final InputStream is = new ByteArrayInputStream(requestBody);

            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return sis;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (br == null) {
            final Charset charset = EncodingHelper.getCharsetByAlias(getCharacterEncoding());
            br = new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }
        return br;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }
}