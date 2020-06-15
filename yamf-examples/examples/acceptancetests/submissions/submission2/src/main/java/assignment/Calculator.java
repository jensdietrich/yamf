package assignment;

/**
 * Some sample class to show testing.
 * The brief is to implement standard addition, but to throw an IllegalArgumentException if an overflow occurs.
 * @author jens dietrich
 */
public class Calculator {

    public static int safelyAdd (int x, int y) {
        int s =  x + y;
        if (s < Math.max(x,y)) {
            throw new IllegalArgumentException();
        }
        else {
            return s;
        }
    }
}
