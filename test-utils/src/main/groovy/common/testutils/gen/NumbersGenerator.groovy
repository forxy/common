package common.testutils.gen

/**
 * Generates different types of numbers
 */
abstract class NumbersGenerator extends AbstractGenerator {

    static String generateNumber(final int size) {
        StringBuilder sb = new StringBuilder(size)
        for (i in 0..size) {
            sb.append(RAND.nextInt(10))
        }
        return sb.toString()
    }

    static String generateNumber(final int size, final boolean allowFirstZeroes) {
        if (allowFirstZeroes) {
            return generateNumber(size)
        }

        StringBuilder sb = new StringBuilder(size)
        sb.append(RAND.nextInt(9) + 1)
        for (int i = 1; i < size; i++) {
            sb.append(RAND.nextInt(10))
        }
        return sb.toString()
    }

    static String generateGUID() {
        return String.valueOf(UUID.randomUUID())
    }

    static Integer generateInt(int min, int max) {
        if (max < min) return 0
        return min + RAND.nextInt(max - min)
    }
}
