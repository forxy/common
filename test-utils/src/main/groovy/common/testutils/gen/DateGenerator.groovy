package common.testutils.gen
/**
 * Generates different types of realistic dates
 */
abstract class DateGenerator extends AbstractGenerator {

    static final long DAY = 86400000

    static Date generateDateInPast(int minDays, int maxDays) {
        Date date = new Date()
        date.time = date.time - minDays * DAY - Math.abs(RAND.nextInt(maxDays - minDays) * DAY)
        return date
    }

    static Date generateDateInFuture(int minDays, int maxDays) {
        Date date = new Date()
        date.time = date.time + minDays * DAY + Math.abs(RAND.nextInt(maxDays - minDays) * DAY)
        return date
    }
}
