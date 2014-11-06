package common.pojo;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Perform Simple Date formatting on date field
 */
public class SimpleJacksonDateSerializer extends JsonSerializer<Date> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void serialize(final Date value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        jgen.writeString(SIMPLE_DATE_FORMAT.format(value));
    }
}
