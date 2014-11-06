package common.logging.extractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Interface to support field extraction extension in endpoint requests.
 * Logging library will apply specified list of field extractors on each Request and Response payload.
 */
public interface IHttpFieldExtractor {
    /**
     * Extract fields, library will add them to Context frame after extraction.
     *
     * @param payload         payload to extract values from
     * @param frame           current Context frame, may be used to extract and mutate fields already present in context
     * @param request         http servlet request object
     * @param response        http servlet response object
     * @param requestHeaders  http request headers
     * @param responseHeaders http response headers
     * @return key value map with extracted fields, it will be added to Context frame
     */
    Map<String, Object> extract(byte[] payload, Map<String, Object> frame, HttpServletRequest request,
                                HttpServletResponse response, Map<String, List<String>> requestHeaders,
                                Map<String, List<String>> responseHeaders);
}