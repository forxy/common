package common.pojo

import org.codehaus.jackson.JsonParser
import org.codehaus.jackson.map.DeserializationContext
import org.codehaus.jackson.map.JsonDeserializer

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Perform Simple Date formatting on date field
 */
class SimpleJacksonDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

    @Override
    Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            return SIMPLE_DATE_FORMAT.parse(jp.text)
        } catch (ParseException e) {
            throw new RuntimeException(e)
        }
    }
}
