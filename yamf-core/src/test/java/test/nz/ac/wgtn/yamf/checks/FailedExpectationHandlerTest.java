package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.ConditionNotSatisfiedHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.*;

public class FailedExpectationHandlerTest {

    @Test
    public void testAssertTrue1() {
        boolean result = ConditionNotSatisfiedHandler.AssertTrue.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssertTrue2() {
        Throwable error = Assertions.assertThrows(AssertionError.class,() -> ConditionNotSatisfiedHandler.AssertTrue.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testAssumeTrue1() {
        boolean result = ConditionNotSatisfiedHandler.AssumeTrue.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssumeTrue2() {
        Throwable error = Assertions.assertThrows(TestAbortedException.class,() -> ConditionNotSatisfiedHandler.AssumeTrue.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateArgument1() {
        boolean result = ConditionNotSatisfiedHandler.ValidateArgument.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateArgument2() {
        Throwable error = Assertions.assertThrows(IllegalArgumentException.class,() -> ConditionNotSatisfiedHandler.ValidateArgument.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testValidateState1() {
        boolean result = ConditionNotSatisfiedHandler.ValidateState.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testValidateState2() {
        Throwable error = Assertions.assertThrows(IllegalStateException.class,() -> ConditionNotSatisfiedHandler.ValidateState.handle(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testIgnore1() {
        boolean result = ConditionNotSatisfiedHandler.Ignore.handle(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testIgnore2() {
        boolean result = ConditionNotSatisfiedHandler.Ignore.handle(false,"foo");
        assertFalse(result);
    }


}
