package common.utils;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Common date utils
 */
public abstract class DateUtils {

    public static XMLGregorianCalendar newXMLGregorianCalendar(final Date date) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    }

    public static Date dateFromXMLGregorianCalendar(final XMLGregorianCalendar xmlGregorianCalendar) throws Exception {
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }
}
