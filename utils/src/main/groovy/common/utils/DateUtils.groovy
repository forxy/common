package common.utils

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

/**
 * Common date utils
 */
abstract class DateUtils {

    static XMLGregorianCalendar newXMLGregorianCalendar(final Date date) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar()
        gregorianCalendar.time = date
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar)
    }

    static Date dateFromXMLGregorianCalendar(final XMLGregorianCalendar xmlGregorianCalendar) throws Exception {
        return xmlGregorianCalendar.toGregorianCalendar().time
    }
}
