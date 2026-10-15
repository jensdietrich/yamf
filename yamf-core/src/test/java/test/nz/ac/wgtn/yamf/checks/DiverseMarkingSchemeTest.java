package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.MarkingScriptBuilder;
import nz.ac.wgtn.yamf.reporting.ConsoleReporter;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiverseMarkingSchemeTest {

    @Test
    public void test () throws Exception {
        File submissionsFolder = Utils.getResourceAsFile("submissions");
        File[] submissions = submissionsFolder.listFiles(f -> f.isDirectory() && !f.isHidden());
        new MarkingScriptBuilder()
            .submissions(submissions)
            .markingScheme(DiverseMarkingScheme.class)
            .reportTo(f -> new ConsoleReporter())
            .run();

        assertTrue(DiverseMarkingScheme.importedExecuted);
    }
}
