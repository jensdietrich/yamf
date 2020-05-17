package nz.ac.wgtn.yamf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Listener for test execution, records results and associated marks extracted from annotations.
 * @author jens dietrich
 */
public class MarkingTestExecutionListener implements TestExecutionListener {

    private static final Logger LOGGER = LogManager.getLogger("marking");

    static class AssignedMark {
        double marks = 0;
        String name = null;
        boolean mustBeMarkedManually = false;
        String manualMarkingInstructions = null;

        public AssignedMark(double marks, String name,boolean mustBeMarkedManually,String manualMarkingInstructions) {
            this.marks = marks;
            this.name = name;
            this.mustBeMarkedManually = mustBeMarkedManually;
            this.manualMarkingInstructions = manualMarkingInstructions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssignedMark that = (AssignedMark) o;
            return Double.compare(that.marks, marks) == 0 &&
                    mustBeMarkedManually == that.mustBeMarkedManually &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(manualMarkingInstructions, that.manualMarkingInstructions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(marks, name, mustBeMarkedManually, manualMarkingInstructions);
        }

        @Override
        public String toString() {
            return "AssignedMark{" +
                    "marks=" + marks +
                    ", name='" + name + '\'' +
                    ", mustBeMarkedManually=" + mustBeMarkedManually +
                    ", manualMarkingInstructions='" + manualMarkingInstructions + '\'' +
                    '}';
        }
    }

    private List<MarkingResultRecord> results = new ArrayList<>();

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        System.out.println("Tests started");
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        System.out.println("Tests finished");
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {

    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        System.out.println("Test skipped " + testIdentifier);
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {

    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        try {
            AssignedMark extractedMark = extractMark(testIdentifier);
            if (extractedMark!=null) {
                results.add(new MarkingResultRecord(extractedMark,testIdentifier,testExecutionResult));
            }
        }
        catch (Exception x) {
            LOGGER.error("Exception extracting mark from test " + testIdentifier.getDisplayName(),x);
        }


    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        System.out.println("Test reported " + testIdentifier);
    }

    // use reflection to extract annotation
    private AssignedMark extractMark (TestIdentifier testIdentifier) throws ClassNotFoundException, NoSuchMethodException {
        if (testIdentifier.getType() == TestDescriptor.Type.TEST && testIdentifier.getSource().isPresent()) {
            TestSource source = testIdentifier.getSource().get();
            if (source instanceof MethodSource) {
                MethodSource methodSource = (MethodSource)source;
                Class clazz = Class.forName(methodSource.getClassName());
                String methodParameterTypes = methodSource.getMethodParameterTypes();
                if (methodParameterTypes==null || methodParameterTypes.equals("")) { // test methods dont have parameters
                    LOGGER.warn("Parameterised methods are not yet supported");
                }
                Method method = clazz.getMethod(methodSource.getMethodName());

                Marking markingAnnotation = method.getAnnotation(Marking.class);
                if (markingAnnotation ==null) {
                    LOGGER.warn("No @Mark annotation found in test " + testIdentifier.getDisplayName());
                    return null;
                }
                else {
                    ManualMarkingIsRequired manualMarkingAnnotation = method.getAnnotation(ManualMarkingIsRequired.class);
                    AssignedMark mark = new AssignedMark(
                            markingAnnotation.marks(),
                            markingAnnotation.name(),
                            manualMarkingAnnotation!=null,
                            manualMarkingAnnotation==null?null:manualMarkingAnnotation.instructions());
                    return mark;
                }
            }

        }
        LOGGER.warn("Cannot extract marking info from test " + testIdentifier.getDisplayName());
        return null;
    }

    public List<MarkingResultRecord> getResults () {
        Collections.sort(this.results, Comparator.comparing(MarkingResultRecord::getName));
        return Collections.unmodifiableList(this.results);
    }

}
