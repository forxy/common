package common.logging.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import common.utils.EncodingHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SpringEL extractor, putting extract(...) params into evaluation context & capable of running EL expressions over it.
 * May be used both to mutate context fields & extract new from other params.
 * Both client & endpoint calls are supported.
 * <p/>
 * Requires SpEL (spring-expression jar Spring 3+)
 * <p/>
 * <p/>
 * See http://static.springsource.org/spring/docs/3.0.x/reference/expressions.html
 * <p/>
 * Configuration sample:
 * <pre>
 *  <bean class="SpringELFieldExtractor">
 *      <property name="extractRules">
 *          <map>
 *              <entry key="OperationName" value="#request.getRequestURI().replaceAll('.*\/(.+)/.*$', '$1')"/>
 *              <entry key="TransactionGUID" value="#requestHeaders['transactionguid']"/>
 *              <entry key="MessageGUID" value="#requestHeaders['messageguid']"/>
 *          </map>
 *      </property>
 *  </bean>
 * </pre>
 */
public class SpringELFieldExtractor implements IHttpFieldExtractor, IFieldExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringELFieldExtractor.class);

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private Map<String, Expression> extractRules;
    private BeanResolver beanResolver;

    @Override
    public Map<String, Object> extract(final byte[] payload, final Map<String, Object> frame) {
        return extractInternal(payload, frame, null, null, null, null);
    }

    @Override
    public Map<String, Object> extract(final byte[] payload, final Map<String, Object> frame,
                                       final HttpServletRequest request, final HttpServletResponse response,
                                       final Map<String, List<String>> requestHeaders,
                                       final Map<String, List<String>> responseHeaders) {
        return extractInternal(payload, frame, request, response, requestHeaders, responseHeaders);
    }


    private Map<String, Object> extractInternal(final byte[] payload, final Map<String, Object> frame,
                                                final HttpServletRequest request, final HttpServletResponse response,
                                                final Map<String, List<String>> requestHeaders,
                                                final Map<String, List<String>> responseHeaders) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        try {
            final StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(beanResolver);
            context.setVariable("payload", EncodingHelper.toUTFString(payload));
            context.setVariable("frame", frame);
            context.setVariable("request", request);
            context.setVariable("response", response);
            context.setVariable("requestHeaders", requestHeaders);
            context.setVariable("responseHeaders", responseHeaders);
            for (final Map.Entry<String, Expression> kv : extractRules.entrySet()) {
                final String field = kv.getKey();
                final Expression expression = kv.getValue();
                try {
                    final Object value = rollupResult(expression.getValue(context));
                    if (value != null) {
                        result.put(field, value);
                    }
                } catch (SpelEvaluationException see) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Unable to eval:" + expression + " to field:" + field, see);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Error while extracting", e);
        }
        return result;
    }

    private Object rollupResult(Object result) {
        if (result instanceof Collection) {
            final Collection collection = (Collection) result;
            if (collection.size() == 0) {
                result = null;
            } else if (collection.size() == 1) {
                result = collection.iterator().next();
            }
        }
        return result;
    }

    /**
     * Set SpringEL extract rules
     *
     * @param extractRules key - new field name, value - el rule
     */
    public void setExtractRules(final Map<String, String> extractRules) {
        this.extractRules = new LinkedHashMap<String, Expression>();
        if (extractRules != null) {
            for (final Map.Entry<String, String> kv : extractRules.entrySet()) {
                this.extractRules.put(kv.getKey(), PARSER.parseExpression(kv.getValue()));
            }
        }
    }

    /**
     * Set bean resolver to be able to use @bean.method(...) in expressions
     *
     * @param beanResolver impl
     */
    public void setBeanResolver(final BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }
}
