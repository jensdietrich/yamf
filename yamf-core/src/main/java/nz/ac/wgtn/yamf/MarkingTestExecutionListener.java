package nz.ac.wgtn.yamf;

import com.google.common.base.Preconditions;
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
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Listener for test execution, records results and associated marks extracted from annotations.
 * @author jens dietrich
 */
public class MarkingTestExecutionListener implements TestExecutionListener {

    private static final Logger LOGGER = LogManager.getLogger("marking");

    public static class AssignedMark {
        double marks = 0;
        String name = null;
        boolean mustBeMarkedManually = false;
        String manualMarkingInstructions = null;
        Method importer = null;
        String importContext = null;

        public AssignedMark(double marks, String name,boolean mustBeMarkedManually,String manualMarkingInstructions,Method importer,String importContext) {
            this.marks = marks;
            this.name = name;
            this.mustBeMarkedManually = mustBeMarkedManually;
            this.manualMarkingInstructions = manualMarkingInstructions;
            this.importer = importer;
            this.importContext = importContext;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssignedMark that = (AssignedMark) o;
            return Double.compare(marks, that.marks) == 0 && mustBeMarkedManually == that.mustBeMarkedManually && Objects.equals(name, that.name) && Objects.equals(manualMarkingInstructions, that.manualMarkingInstructions) && Objects.equals(importer, that.importer) && Objects.equals(importContext, that.importContext);
        }

        @Override
        public int hashCode() {
            return Objects.hash(marks, name, mustBeMarkedManually, manualMarkingInstructions, importer, importContext);
        }
    }

    private List<MarkingResultRecord> results = new ArrayList<>();

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        LOGGER.info("Tests started");
        Attachments.reset();
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        LOGGER.info("Tests finished");
        Attachments.reset();
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {

    }

    private boolean isTestMethod (TestIdentifier testIdentifier) {
        return testIdentifier.getSource().isPresent() && testIdentifier.getSource().get() instanceof MethodSource;
    }

    private String getTestIdentifierName(TestIdentifier testIdentifier) {
        TestSource source = testIdentifier.getSource().isPresent() ?
                testIdentifier.getSource().get() : null;
        if (source!=null && source instanceof MethodSource) {
            String name = "";
            MethodSource methodSource = (MethodSource)source;
            try {
                name = methodSource.getClassName() + "::";
            }
            catch (Exception x) {
            }
            return name + methodSource.getMethodName();
        }
        return "testidentifier " + testIdentifier;
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        if (isTestMethod(testIdentifier)) {
            LOGGER.info("check " + getTestIdentifierName(testIdentifier) + " skipped, reason: " + reason);
            Attachments.startTest(testIdentifier); // needs to be checked
        }
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (isTestMethod(testIdentifier)) {
            LOGGER.info("running check: " + getTestIdentifierName(testIdentifier));
            Attachments.startTest(testIdentifier);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (isTestMethod(testIdentifier)) {
            LOGGER.info("\tTest " + getTestIdentifierName(testIdentifier) + " finished");
            try {
                AssignedMark extractedMark = extractMark(testIdentifier);
                if (extractedMark != null) {
                    MarkingResultRecord record = new MarkingResultRecord(extractedMark, testIdentifier, testExecutionResult,extractedMark.importer, extractedMark.importContext);
                    Collection<Attachment> attachments = Attachments.getAttachments(testIdentifier);
                    record.setAttachments(attachments);
                    results.add(record);

                    LOGGER.info("\tstatus: " + record.getResultStatus());
                }
            } catch (Exception x) {
                LOGGER.error("Exception extracting mark from test " + testIdentifier.getDisplayName(), x);
            }
            finally {
                Attachments.endTest(testIdentifier);
            }
        }
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
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
                    LOGGER.debug("Parameterised methods are not yet supported");
                }
                Method method = clazz.getMethod(methodSource.getMethodName());

                Marking markingAnnotation = method.getAnnotation(Marking.class);
                if (markingAnnotation ==null) {
                    LOGGER.warn("No @Mark annotation found in test " + testIdentifier.getDisplayName());
                    return null;
                }
                else {
                    ManualMarkingIsRequired manualMarkingAnnotation = method.getAnnotation(ManualMarkingIsRequired.class);
                    boolean manualMarkingRequired = manualMarkingAnnotation!=null;
                    ImportMark importMarkAnnotation = method.getAnnotation(ImportMark.class);
                    Method importer = null;
                    String importContext = null;
                    if (importMarkAnnotation!=null) {
                        try {
                            Class<?> cl = importMarkAnnotation.clazz();
                            importer = cl.getMethod(importMarkAnnotation.method(),String.class);
                            String importedAsString = cl.getName() + "::" + importMarkAnnotation.method();
                            Preconditions.checkState(importer.getParameterTypes().length==1,"importer " + importedAsString + " must not a single argument");
                            Preconditions.checkState(importer.getParameterTypes()[0]==String.class,"importer " + importedAsString + " single argument must be of type String");
                            Preconditions.checkState(importer.getReturnType()==Double.TYPE || importer.getReturnType()==ExplainableGrade.class,"importer " + importedAsString + " must return a double or ExplainableGrade");
                            Preconditions.checkState( Modifier.isStatic(importer.getModifiers()),"importer " + importedAsString + " must be static");

                            importContext = importMarkAnnotation.context();
                        }
                        catch (Exception x) {
                            LOGGER.error("Error running importer " + importMarkAnnotation.clazz() + "::" + importMarkAnnotation.method(),x);
                            // switch to manual marking
                            manualMarkingRequired = true;
                        }
                    }
                    AssignedMark mark = new AssignedMark(
                        markingAnnotation.marks(),
                        markingAnnotation.name(),
                        manualMarkingRequired,
                        manualMarkingAnnotation==null?null:manualMarkingAnnotation.instructions(),
                        importer,
                        importContext
                    );
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
