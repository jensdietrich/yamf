package nz.ac.wgtn.yamf.examples.mvn;

import nz.ac.wgtn.yamf.MarkingScriptBuilder;
import nz.ac.wgtn.yamf.reporting.MSWordReporter;

import java.io.File;

/**
 * Example showing how to write a marking script.
 * @author jens dietrich
 */
public class MarkingScript extends MarkingScriptBuilder {

    public static void main(String[] args) throws Exception {
        new MarkingScriptBuilder()
            .submissions(new File("yamf-examples/examples/mvn-static/submissions").listFiles())
            .markingScheme(MarkingScheme.class)
            .beforeMarkingEachProjectDo(submission -> MarkingScheme.submission = submission) // inject project folder !!
            .afterMarkingEachActionDo(submission -> System.out.println("Done marking " + submission.getAbsolutePath()))
            .reportTo(submission -> new MSWordReporter("yamf-examples/sample-reports/example-mvn-marks-" + submission.getName() + ".doc"))
            .run();
    }
}