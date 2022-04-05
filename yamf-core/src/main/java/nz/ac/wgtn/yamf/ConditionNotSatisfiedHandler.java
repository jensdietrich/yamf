package nz.ac.wgtn.yamf;


import com.google.common.base.Preconditions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import javax.annotation.Nonnull;

/**
 * The failed expectation handler can be used in APIs to decide how to react to failed conditions (usually preconditions).
 * Options are failed test assertions, failed test assumptions, or throwing a runtime exception.
 * None of those require the declaration of checked exceptions as they all result in errors or runtime exceptions.
 * Example: assume there is a method to get a file names aFile in a Folder aFolder.
 * Then a reasonable precondition check might be that aFolder.exists. The handler would decide how to handle this,
 * which would typically mean throwing an exception or error. Note that yamf handles failed assertions and assumptions
 * very differently: when an assumption fails, a check will be flagged for manual testing, when an assertion fails, a
 * check will fail.
 * So the correct code would be FailedExpectationHandler handler = ..;  handler.handler(aFolder.exists,"Folder does not exist: " + aFolder);
 * The Ignore handler does not react to failed tests, this can be used to return null, e.g.:
 * if (!handler.handle(aFolder.exists,"Folder does not exist: " + aFolder)) return null;
 * @author jens dietrich
 */
public abstract class ConditionNotSatisfiedHandler {

    public static ConditionNotSatisfiedHandler DEFAULT = ConditionNotSatisfiedHandler.AssumeTrue;

    public static void install(@Nonnull ConditionNotSatisfiedHandler instance) {
        ConditionNotSatisfiedHandler.DEFAULT = instance;
    }

    public static ConditionNotSatisfiedHandler AssumeTrue = new ConditionNotSatisfiedHandler() {
        @Override
        public boolean handle(boolean condition, String conditionHasFailedMessage) {
            Assumptions.assumeTrue(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static ConditionNotSatisfiedHandler AssertTrue = new ConditionNotSatisfiedHandler() {
        @Override
        public boolean handle(boolean condition, String conditionHasFailedMessage) {
            Assertions.assertTrue(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static ConditionNotSatisfiedHandler ValidateArgument = new ConditionNotSatisfiedHandler() {
        @Override
        public boolean handle(boolean condition, String conditionHasFailedMessage) {
            Preconditions.checkArgument(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static ConditionNotSatisfiedHandler ValidateState = new ConditionNotSatisfiedHandler() {
        @Override
        public boolean handle(boolean condition, String conditionHasFailedMessage) {
            Preconditions.checkState(condition,conditionHasFailedMessage);
            return condition;
        }
    };

    public static ConditionNotSatisfiedHandler Ignore = new ConditionNotSatisfiedHandler() {
        @Override
        public boolean handle(boolean condition, String conditionHasFailedMessage) {
            return condition;
        }
    };

    static {
        install(ConditionNotSatisfiedHandler.AssumeTrue);
    }

    /**
     * Handle a failed condition.
     * @param condition
     * @param conditionHasFailedMessage
     * @return the result of the condition check if the return statement is reached (in the Ignore), this can be used at the callsite
     * e.g. to return null: if (!handle(..)) return null;
     */
    public abstract boolean handle (boolean condition,String conditionHasFailedMessage);


}
