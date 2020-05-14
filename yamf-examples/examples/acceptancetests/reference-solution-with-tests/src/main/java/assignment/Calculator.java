package assignment;

/**
 * Some sample class to show testing.
 * The brief is to implement standard addition, but to throw an IllegalArgumentException if an overflow occurs.
 * @author jens dietrich
 */
public class Calculator {

    public static int safelyAdd (int x, int y) {
        // logic from http://svn.apache.org/viewvc/commons/proper/math/trunk/src/main/java/org/apache/commons/math3/util/ArithmeticUtils.java?view=markup#l40
        long s = (long)x + (long)y;
        if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        return (int)s;
    }
}
