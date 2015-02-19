package common.exceptions

import org.slf4j.Logger
import org.slf4j.MDC
import spock.lang.Specification

/**
 * Error handling test suite
 */
class ErrorHandlingSpec extends Specification {

    def "Trace should be successfully logged with test parameters"() {
        given:
        Logger logger = Mock(Logger) {
            isTraceEnabled() >> true
        }
        GroovySpy(MDC, global: true)

        when:
        new ServiceException(TestEvent.Trace, 'test').log(logger)

        then:
        MDC.getCopyOfContextMap() >> ['test' : 'test']
        1 * logger.trace('EventID=1000 EventCode=Trace [test:test]\n Trace with param: \'test\'', _ as ServiceException)
    }

    def "Info should be successfully logged with test parameters"() {
        given:
        Logger logger = Mock(Logger) {
            isInfoEnabled() >> true
        }
        GroovySpy(MDC, global: true)

        when:
        new ServiceException(TestEvent.Info, 'test').log(logger)

        then:
        MDC.getCopyOfContextMap() >> ['test' : 'test']
        1 * logger.info('EventID=1001 EventCode=Info [test:test]\n Info with param: \'test\'', _ as ServiceException)
    }

    def "Warn should be successfully logged with test parameters"() {
        given:
        Logger logger = Mock(Logger) {
            isWarnEnabled() >> true
        }
        GroovySpy(MDC, global: true)

        when:
        new ServiceException(TestEvent.Warn, 'test').log(logger)

        then:
        MDC.getCopyOfContextMap() >> ['test' : 'test']
        1 * logger.warn('EventID=1002 EventCode=Warn [test:test]\n Warn with param: \'test\'', _ as ServiceException)
    }

    def "Error should be successfully logged with test parameters"() {
        given:
        Logger logger = Mock(Logger) {
            isErrorEnabled() >> true
        }
        GroovySpy(MDC, global: true)

        when:
        new ServiceException(TestEvent.Error, 'test1', 'test2').log(logger)

        then:
        MDC.getCopyOfContextMap() >> ['test' : 'test']
        1 * logger.error('EventID=1003 EventCode=Error [test:test]\n Error with params: \'test1\', \'test2\'', _ as ServiceException)
    }

    def "Error log should include external exception message"() {
        given:
        Logger logger = Mock(Logger) {
            isErrorEnabled() >> true
        }
        GroovySpy(MDC, global: true)

        when:
        new ServiceException(new Exception("Message"), TestEvent.Error, 'test1', 'test2').log(logger)

        then:
        MDC.getCopyOfContextMap() >> ['test' : 'test']
        1 * logger.error('EventID=1003 EventCode=Error Details=Message [test:test]\n Error with params: \'test1\', \'test2\'', _ as ServiceException)
    }

    def "Exception util should find cause of the certain exception class"() {
        given:
        ServiceException target2 = new ServiceException(TestEvent.Error)
        IllegalStateException target1 = new IllegalStateException(target2)
        Throwable th = new Exception(target1)

        expect:
        ExceptionUtils.findCause(th, IllegalStateException.class) == target1
        ExceptionUtils.findCause(th, ServiceException.class) == target2
    }
}
