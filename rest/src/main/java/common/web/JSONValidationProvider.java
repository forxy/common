package common.web;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.IValidator;
import net.sf.oval.exception.ValidationFailedException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;
import common.exceptions.ValidationException;
import common.rest.client.transport.support.ObjectMapperProvider;
import common.support.Configuration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom JSON provider with validation step
 */
public class JSONValidationProvider extends JacksonJsonProvider {

    public static enum Configs {
        IsObjectValidationEnabled
    }

    private IValidator validator;
    private boolean isObjectValidationEnabled = false;

    public JSONValidationProvider() throws IOException {
        super();
        setMapper(ObjectMapperProvider.getDefaultMapper());
    }

    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations,
                           final MediaType mediaType, final MultivaluedMap<String, String> headers,
                           final InputStream is) throws IOException {
        final Object value;
        try {
            value = super.readFrom(type, genericType, annotations, mediaType, headers, is);
        } catch (JsonParseException | JsonMappingException e) {
            throw new ValidationException(e);
        }

        // Apply validation
        if (isObjectValidationEnabled) {
            try {
                List<ConstraintViolation> violations;
                violations = validator.validate(value);
                if (violations != null && violations.size() > 0) {

                    final List<String> messages = new ArrayList<String>(violations.size());

                    for (ConstraintViolation violation : violations) {
                        messages.add(violation.getMessage());
                    }

                    throw new ValidationException(messages);
                }
            } catch (ValidationFailedException e) {
                throw ValidationException.build(e);
            }
        }
        return value;
    }

    public void setValidator(final IValidator validator) {
        this.validator = validator;
    }

    public void setConfiguration(final Configuration configuration) {
        if (configuration != null) {
            isObjectValidationEnabled = configuration.getBoolean(Configs.IsObjectValidationEnabled);
        }
    }
}