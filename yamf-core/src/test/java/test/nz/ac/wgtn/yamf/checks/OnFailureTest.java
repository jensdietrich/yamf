package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.OnFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OnFailureTest {

    @Test
    public void testAssertTrue1() {
        boolean result = OnFailure.MARK_AS_FAILED.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssertTrue2() {
        Throwable error = Assertions.assertThrows(AssertionError.class,() -> OnFailure.MARK_AS_FAILED.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testAssumeTrue1() {
        boolean result = OnFailure.MARK_MANUALLY.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testAssumeTrue2() {
        Throwable error = Assertions.assertThrows(TestAbortedException.class,() -> OnFailure.MARK_MANUALLY.check(false,"foo"));
        assertTrue(error.getMessage().contains("foo"));
    }

    @Test
    public void testIgnore1() {
        boolean result = OnFailure.CARRY_ON.check(true,"foo");
        assertTrue(result);
    }

    @Test
    public void testIgnore2() {
        boolean result = OnFailure.CARRY_ON.check(false,"foo");
        assertFalse(result);
    }


}
