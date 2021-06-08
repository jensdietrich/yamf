package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.FailedExpectationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.*;

public class FailedExpectationHandlerTest {

    @Test
    public void testAssertTrue1() {
        boolean result = FailedExpectationHandler.AssertTrue.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssertTrue2() {
        Throwable error = Assertions.assertThrows(AssertionError.class,() -> FailedExpectationHandler.AssertTrue.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testAssumeTrue1() {
        boolean result = FailedExpectationHandler.AssumeTrue.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssumeTrue2() {
        Throwable error = Assertions.assertThrows(TestAbortedException.class,() -> FailedExpectationHandler.AssumeTrue.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateArgument1() {
        boolean result = FailedExpectationHandler.ValidateArgument.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateArgument2() {
        Throwable error = Assertions.assertThrows(IllegalArgumentException.class,() -> FailedExpectationHandler.ValidateArgument.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateState1() {
        boolean result = FailedExpectationHandler.ValidateState.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateState2() {
        Throwable error = Assertions.assertThrows(IllegalStateException.class,() -> FailedExpectationHandler.ValidateState.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testIgnore1() {
        boolean result = FailedExpectationHandler.Ignore.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testIgnore2() {
        boolean result = FailedExpectationHandler.Ignore.handle(false,"foo");
        assertFalse(result);
    }


}
