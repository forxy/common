package common.web

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import common.exceptions.ValidationException
import common.rest.client.transport.support.ObjectMapperProvider
import common.utils.support.Configuration
import net.sf.oval.ConstraintViolation
import net.sf.oval.IValidator
import net.sf.oval.exception.ValidationFailedException

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import java.lang.annotation.Annotation
import java.lang.reflect.Type

/**
 * Custom JSON provider with validation step
 */
class JSONValidationProvider extends JacksonJsonProvider {

    static enum Configs {
        IsObjectValidationEnabled
    }

    IValidator validator
    private boolean isObjectValidationEnabled = false

    JSONValidationProvider() throws IOException {
        super()
        setMapper(ObjectMapperProvider.defaultMapper)
    }

    @Override
    Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations,
                    final MediaType mediaType, final MultivaluedMap<String, String> headers,
                    final InputStream is) throws IOException {
        final Object value
        try {
            value = super.readFrom(type, genericType, annotations, mediaType, headers, is)
        } catch (JsonParseException | JsonMappingException e) {
            throw new ValidationException(e)
        }

        // Apply validation
        if (isObjectValidationEnabled) {
            try {
                List<ConstraintViolation> violations = validator.validate value
                if (violations) {
                    throw new ValidationException(violations.collect { it.message })
                }
            } catch (ValidationFailedException e) {
                throw ValidationException.build(e)
            }
        }
        return value
    }

    void setConfiguration(final Configuration configuration) {
        if (configuration != null) {
            isObjectValidationEnabled = configuration.getBoolean(Configs.IsObjectValidationEnabled)
        }
    }
}