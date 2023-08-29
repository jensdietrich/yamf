package nz.ac.wgtn.yamf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;

/**
 * The failed expectation handler can be used in APIs to decide how to react to failed conditions (usually preconditions).
 * Options are failed test assertions, failed test assumptions, or throwing a runtime exception.
 * So in this sense an expectation can be a precondition as well as a post condition.
 * None of those require the declaration of checked exceptions as they all result in errors or runtime exceptions.
 * Example: assume there is a method to get a file named aFile in a Folder aFolder.
 * Then a reasonable precondition check might be that aFolder.exists. The handler would decide how to handle this,
 * which would typically mean throwing an exception or error. Note that yamf handles failed assertions and assumptions
 * very differently: when an assumption fails, a check will be flagged for manual testing, when an assertion fails, a
 * check will fail.
 * So the correct code would be ExpectationChecker handler = ..;  handler.handler(aFolder.exists,"Folder does not exist: " + aFolder);
 * The CARRY_ON handler does not react to failed tests, this can be used to return null, e.g.:
 * if (!handler.handle(aFolder.exists,"Folder does not exist: " + aFolder)) return null;*
 * @author jens dietrich
 */
public abstract class OnFailure {


    public static OnFailure MARK_MANUALLY = new OnFailure() {

        @Override
        public void handle(Exception x, String message) {
            throw new TestAbortedException(message,x);
        }

        @Override
        public boolean check(boolean condition, String conditionHasFailedMessage) {
            Assumptions.assumeTrue(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static OnFailure MARK_AS_FAILED = new OnFailure() {
        @Override
        public void handle(Exception x, String message) {
            throw new AssertionError(message,x);
        }

        @Override
        public boolean check(boolean condition, String conditionHasFailedMessage) {
            Assertions.assertTrue(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static OnFailure CARRY_ON = new OnFailure() {
        @Override
        public boolean check(boolean condition, String conditionHasFailedMessage) {
            return condition;
        }

        @Override
        public void handle(Exception x, String message) {
            // nothing todo
        }
    };

    public static OnFailure DEFAULT = MARK_AS_FAILED;

    /**
     * Check an expectation.
     * @param condition
     * @param checkFailedMessage
     * @return the result of the condition check if the return statement is reached (in the Ignore), this can be used at the callsite
     * e.g. to return null: if (!handle(..)) return null;
     */
    public abstract boolean check(boolean condition, String checkFailedMessage);

    /**
     * Handle an exception.
     * @param x
     * @param message
     */
    public abstract void handle(Exception x,String message);
}
