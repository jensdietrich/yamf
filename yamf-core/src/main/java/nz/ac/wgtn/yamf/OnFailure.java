package nz.ac.wgtn.yamf;

public abstract class OnFailure extends ExpectationChecker {

    public static ExpectationChecker MARK_MANUALLY= AssumeTrue;
    public static ExpectationChecker MARK_AS_FAILED = AssertTrue;
    public static ExpectationChecker CARRY_ON = Ignore;
}
