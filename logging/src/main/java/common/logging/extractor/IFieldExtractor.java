package common.logging.extractor;

/**
 * Created by Uladzislau_Prykhodzk on 11/12/13.
 */

import java.util.Map;

/**
 * Interface to support field extraction extension in client requests.
 * Logging library will apply specified list of field extractors on each Request and Response payload.
 */
public interface IFieldExtractor {
    /**
     * Extract fields, library will add them to Context frame after extraction.
     *
     * @param payload payload to extract values from
     * @param frame   current Context frame, may be used to extract and mutate fields already present in context
     * @return key value map with extracted fields, it will be added to Context frame
     */
    Map<String, Object> extract(byte[] payload, Map<String, Object> frame);
}
