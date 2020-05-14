package acceptancetests;

import assignment.Calculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCalculatorOverflow {

    @Test
    public void testOverflow () throws Exception {

       // URL location = Calculator.class.getResource('/' + Calculator.class.getName().replace('.', '/') + ".class");
       // System.out.println("testing " + Calculator.class + " defined here: " + location);
       // Assertions.fail("testing " + Calculator.class + " defined here: " + location);
        assertThrows(IllegalArgumentException.class,() -> Calculator.safelyAdd(2_000_000_000,1_000_000_000));
    }

}
