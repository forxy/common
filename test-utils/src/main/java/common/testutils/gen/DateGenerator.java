package common.testutils.gen;

import java.util.Date;

/**
 * Generates different types of realistic dates
 */
public abstract class DateGenerator extends AbstractGenerator {

    public static final long DAY = 86400000;

    public static Date generateDateInPast(int minDays, int maxDays) {
        Date date = new Date();
        date.setTime(date.getTime() - minDays * DAY - Math.abs(RAND.nextInt(maxDays - minDays) * DAY));
        return date;
    }

    public static Date generateDateInFuture(int minDays, int maxDays) {
        Date date = new Date();
        date.setTime(date.getTime() + minDays * DAY + Math.abs(RAND.nextInt(maxDays - minDays) * DAY));
        return date;
    }
}
