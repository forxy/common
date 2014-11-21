package common.logging.wrapper

import org.apache.commons.io.output.TeeOutputStream

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class HttpResponseWrapper extends HttpServletResponseWrapper {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(4096)

    PrintWriter writer
    ServletOutputStream outputStream

    int responseStatus = SC_OK
    String responseRedirectURL
    final Map<String, List<String>> responseHeaders = new LinkedHashMap<String, List<String>>()

    HttpResponseWrapper(final HttpServletResponse response) {
        super(response)
    }

    @Override
    void setStatus(final int sc) {
        responseStatus = sc
        super.setStatus(sc)
    }

    @Override
    void setStatus(final int sc, final String sm) {
        responseStatus = sc
        super.setStatus(sc, sm)
    }

    @Override
    void sendError(final int sc) throws IOException {
        responseStatus = sc
        super.sendError(sc)
    }

    @Override
    void sendError(final int sc, final String msg) throws IOException {
        responseStatus = sc
        super.sendError(sc, msg)
    }

    @Override
    void sendRedirect(final String location) throws IOException {
        responseRedirectURL = location
        responseStatus = SC_MOVED_TEMPORARILY
        super.sendRedirect(location)
    }

    @Override
    void setHeader(final String name, final String value) {
        responseHeaders.put(name, Collections.singletonList(value))
        super.setHeader(name, value)
    }

    @Override
    void addHeader(final String name, final String value) {
        List<String> values = responseHeaders.get(name)
        if (values != null) {
            values.add(value)
        } else {
            values = new ArrayList<String>()
            values.add(value)
            responseHeaders.put(name, values)
        }
        super.addHeader(name, value)
    }

    @Override
    PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new TeePrintWriter(super.writer,
                    new PrintWriter(new OutputStreamWriter(bos, characterEncoding)))
        }
        return writer
    }

    @Override
    ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            final TeeOutputStream tos = new TeeOutputStream(super.outputStream, bos)
            outputStream = new ServletOutputStream() {
                @Override
                void flush() throws IOException {
                    tos.flush()
                }

                @Override
                void close() throws IOException {
                    tos.close()
                }

                @Override
                void write(final int arg) throws IOException {
                    tos.write(arg)
                }
            }
        }
        return outputStream
    }

    byte[] getResponseBody() {
        byte[] result = bos.toByteArray()
        result = result.length > 0 ? result : null
        return result
    }
}