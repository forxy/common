package common.logging.exceptions

import common.exceptions.ServiceException
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

/**
 * DatabaseExceptionHandler class specification
 */
@Subject(DatabaseExceptionHandler)
@Stepwise
class DatabaseExceptionHandlerSpec extends Specification {

    DatabaseExceptionHandler handler = new DatabaseExceptionHandler(databaseHost: 'host')

    def "To handle ServiceException throws it back"() {
        when:
        handler.handleException(new ServiceException(LoggingCommonEvent.ServiceTimeout))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.ServiceTimeout
    }

    def "To handle ConnectException throws ServiceException with eventID=DatabaseIsNotAvailable"() {
        when:
        handler.handleException(new Exception(new ConnectException()))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.DatabaseIsNotAvailable
    }

    def "To handle SocketTimeoutException throws ServiceException with eventID=DatabaseTimeout"() {
        when:
        handler.handleException(new Exception(new SocketTimeoutException()))
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.DatabaseTimeout
    }

    def "To handle any other exception throws ServiceException with eventID=UnknownDataBaseException"() {
        when:
        handler.handleException(new IllegalStateException())
        then:
        def e = thrown(ServiceException)
        e.eventLogID == LoggingCommonEvent.UnknownDataBaseException
    }
}
