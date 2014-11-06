package common.spring;

import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AbstractGenericContextLoader;

/**
 * Groovy beans loader for unit tests autowiring
 */
public class GenericGroovyContextLoader extends AbstractGenericContextLoader {

    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(
            GenericApplicationContext context) {
        return new GroovyBeanDefinitionReader(context);
    }

    @Override
    protected String getResourceSuffix() {
        return ".groovy";
    }
}