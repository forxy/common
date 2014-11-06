package common.logging.wrapper;

import org.apache.commons.io.output.TeeOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);

    private PrintWriter pw;
    private ServletOutputStream sos;

    private int responseStatus = HttpServletResponse.SC_OK;
    private String responseRedirectURL;
    private final Map<String, List<String>> responseHeaders = new LinkedHashMap<String, List<String>>();

    public HttpResponseWrapper(final HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(final int sc) {
        responseStatus = sc;
        super.setStatus(sc);
    }

    @Override
    public void setStatus(final int sc, final String sm) {
        responseStatus = sc;
        super.setStatus(sc, sm);
    }

    @Override
    public void sendError(final int sc) throws IOException {
        responseStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(final int sc, final String msg) throws IOException {
        responseStatus = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void sendRedirect(final String location) throws IOException {
        responseRedirectURL = location;
        responseStatus = HttpServletResponse.SC_MOVED_TEMPORARILY;
        super.sendRedirect(location);
    }

    @Override
    public void setHeader(final String name, final String value) {
        responseHeaders.put(name, Collections.singletonList(value));
        super.setHeader(name, value);
    }

    @Override
    public void addHeader(final String name, final String value) {
        List<String> values = responseHeaders.get(name);
        if (values != null) {
            values.add(value);
        } else {
            values = new ArrayList<String>();
            values.add(value);
            responseHeaders.put(name, values);
        }
        super.addHeader(name, value);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (pw == null) {
            pw = new TeePrintWriter(super.getWriter(),
                    new PrintWriter(new OutputStreamWriter(bos, getCharacterEncoding())));
        }
        return pw;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (sos == null) {
            final TeeOutputStream tos = new TeeOutputStream(super.getOutputStream(), bos);
            sos = new ServletOutputStream() {
                @Override
                public void flush() throws IOException {
                    tos.flush();
                }

                @Override
                public void close() throws IOException {
                    tos.close();
                }

                @Override
                public void write(final int arg) throws IOException {
                    tos.write(arg);
                }
            };
        }
        return sos;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public byte[] getResponseBody() {
        byte[] result = bos.toByteArray();
        result = result.length > 0 ? result : null;
        return result;
    }

    public String getResponseRedirectURL() {
        return responseRedirectURL;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }
}