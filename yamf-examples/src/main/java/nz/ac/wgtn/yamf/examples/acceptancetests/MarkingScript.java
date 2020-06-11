package nz.ac.wgtn.yamf.examples.acceptancetests;

import nz.ac.wgtn.yamf.MarkingScriptBuilder;
import nz.ac.wgtn.yamf.reporting.msoffice.MSExcelReporter;
import nz.ac.wgtn.yamf.reporting.msoffice.MSWordReporter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import java.io.File;

/**
 * Example showing how to write a marking script.
 * @author jens dietrich
 */
public class MarkingScript {

    public static void main(String[] args) throws Exception {

        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);

        new MarkingScriptBuilder()
            .submissions(new File("yamf-examples/examples/acceptancetests/submissions").listFiles())
            .markingScheme(MarkingScheme.class)
            .beforeMarkingEachProjectDo(submission -> MarkingScheme.submission = submission) // inject project folder !!
            .afterMarkingEachActionDo(submission -> System.out.println("Done marking " + submission.getAbsolutePath()))
            .reportTo(submission -> new MSWordReporter("yamf-examples/sample-reports/example-acceptancetests-marks-" + submission.getName() + ".doc"))
            .reportTo(submission -> new MSExcelReporter("yamf-examples/sample-reports/example-acceptancetests-marks-" + submission.getName() + ".xlsx"))
            .run();
    }
}
