package common.logging.extractor

import common.utils.EncodingHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.expression.BeanResolver
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.SpelEvaluationException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
class SpringELFieldExtractor implements IHttpFieldExtractor, IFieldExtractor {
    static final Logger LOGGER = LoggerFactory.getLogger(SpringELFieldExtractor.class)

    static final ExpressionParser PARSER = new SpelExpressionParser()

    private Map<String, Expression> extractRules
    BeanResolver beanResolver

    @Override
    Map<String, Object> extract(final byte[] payload, final Map<String, Object> frame) {
        return extractInternal(payload, frame, null, null, null, null)
    }

    @Override
    Map<String, Object> extract(final byte[] payload, final Map<String, Object> frame,
                                final HttpServletRequest request, final HttpServletResponse response,
                                final Map<String, List<String>> requestHeaders,
                                final Map<String, List<String>> responseHeaders) {
        return extractInternal(payload, frame, request, response, requestHeaders, responseHeaders)
    }


    Map<String, Object> extractInternal(final byte[] payload, final Map<String, Object> frame,
                                        final HttpServletRequest request, final HttpServletResponse response,
                                        final Map<String, List<String>> requestHeaders,
                                        final Map<String, List<String>> responseHeaders) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>()
        try {
            final StandardEvaluationContext context = new StandardEvaluationContext()
            context.setBeanResolver(beanResolver)
            context.setVariable('payload', EncodingHelper.toUTFString(payload))
            context.setVariable('frame', frame)
            context.setVariable('request', request)
            context.setVariable('response', response)
            context.setVariable('requestHeaders', requestHeaders)
            context.setVariable('responseHeaders', responseHeaders)
            extractRules.each { field, expression ->
                try {
                    final Object value = rollupResult(expression.getValue(context))
                    if (value) {
                        result.put(field, value)
                    }
                } catch (SpelEvaluationException see) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Unable to eval:$expression to field:$field", see)
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug('Error while extracting', e)
        }
        return result
    }

    static Object rollupResult(Object result) {
        if (result instanceof Collection) {
            final Collection collection = (Collection) result
            if (collection.size() == 0) {
                result = null
            } else if (collection.size() == 1) {
                result = collection.iterator().next()
            }
        }
        return result
    }

    /**
     * Set SpringEL extract rules
     *
     * @param extractRules key - new field name, value - el rule
     */
    void setExtractRules(final Map<String, String> extractRules) {
        this.extractRules = [:]
        extractRules?.each { key, value ->
            this.extractRules[key] = PARSER.parseExpression(value)
        }
    }
}
