package common.logging.exceptions

import common.exceptions.ServiceException
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

/**
 * ServiceExceptionHandler class specification
 */
@Subject(ServiceExceptionHandler)
@Stepwise
class ServiceExceptionHandlerSpec extends Specification {

    ServiceExceptionHandler handler = new ServiceExceptionHandler()

    def "To handle ServiceException throws it back"() {
        when:
        handler.handleException(new ServiceException(LoggingCommonEvent.ServiceTimeout))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.ServiceTimeout
    }

    def "To handle ConnectException throws ServiceException with eventID=ServiceIsNotAvailable"() {
        when:
        handler.handleException(new Exception(new ConnectException()))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.ServiceIsNotAvailable
    }

    def "To handle SocketTimeoutException throws ServiceException with eventID=ServiceTimeout"() {
        when:
        handler.handleException(new Exception(new SocketTimeoutException()))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.ServiceTimeout
    }

    def "To handle any other exception throws ServiceException with eventID=UnknownServiceException"() {
        when:
        handler.handleException(new IllegalStateException())
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.UnknownServiceException
    }
}
