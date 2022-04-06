package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.ExpectationChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.*;

public class ExpectationCheckerTest {

    @Test
    public void testAssertTrue1() {
        boolean result = ExpectationChecker.AssertTrue.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssertTrue2() {
        Throwable error = Assertions.assertThrows(AssertionError.class,() -> ExpectationChecker.AssertTrue.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testAssumeTrue1() {
        boolean result = ExpectationChecker.AssumeTrue.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssumeTrue2() {
        Throwable error = Assertions.assertThrows(TestAbortedException.class,() -> ExpectationChecker.AssumeTrue.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateArgument1() {
        boolean result = ExpectationChecker.ValidateArgument.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateArgument2() {
        Throwable error = Assertions.assertThrows(IllegalArgumentException.class,() -> ExpectationChecker.ValidateArgument.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateState1() {
        boolean result = ExpectationChecker.ValidateState.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateState2() {
        Throwable error = Assertions.assertThrows(IllegalStateException.class,() -> ExpectationChecker.ValidateState.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testIgnore1() {
        boolean result = ExpectationChecker.Ignore.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testIgnore2() {
        boolean result = ExpectationChecker.Ignore.check(false,"foo");
        assertFalse(result);
    }


}
