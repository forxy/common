package common.testutils.gen;

/**
 * Generates different types of numbers
 */
public abstract class NumbersGenerator extends AbstractGenerator {

    public static String generateNumber(final int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(RAND.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateNumber(final int size, final boolean allowFirstZeroes) {
        if (allowFirstZeroes) {
            return generateNumber(size);
        }

        StringBuilder sb = new StringBuilder(size);
        sb.append(RAND.nextInt(9) + 1);
        for (int i = 1; i < size; i++) {
            sb.append(RAND.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateGUID() {
        return String.valueOf(java.util.UUID.randomUUID());
    }

    public static Integer generateInt(int min, int max) {
        if (max < min) return 0;
        return min + RAND.nextInt(max - min);
    }
}
