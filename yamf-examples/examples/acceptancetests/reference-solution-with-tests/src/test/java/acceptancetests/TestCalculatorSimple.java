package acceptancetests;

import assignment.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class TestCalculatorSimple {

    @Test
    public void test1 () {
        assertSame(4, Calculator.safelyAdd(2,2));
    }

    @Test
    public void test2 () {
        assertSame(2, Calculator.safelyAdd(0,2));
    }

    @Test
    public void test3 () {
        assertSame(-1, Calculator.safelyAdd(2,-3));
    }

}
