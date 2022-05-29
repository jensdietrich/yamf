package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.MarkingScriptBuilder;
import nz.ac.wgtn.yamf.reporting.ConsoleReporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.fail;

public class MarkingSchemeResetTest {

    @Test
    public void testDoReset () throws Exception {
        File submissionsFolder = Utils.getResourceAsFile("submissions");
        File[] submissions = submissionsFolder.listFiles(f -> f.isDirectory() && !f.isHidden());
        new MarkingScriptBuilder()
            .submissions(submissions)
            .markingScheme(MarkingScheme.class)
            .beforeMarkingEachProjectDo(f -> {
                if (MarkingScheme.CACHE!=null) {
                    fail("CACHE should be reset to null");
                }
            })
            .reportTo(f -> new ConsoleReporter())
            .run();
    }

    @Test
    public void testDontReset () throws Exception {
        File submissionsFolder = Utils.getResourceAsFile("submissions");
        File[] submissions = submissionsFolder.listFiles(f -> f.isDirectory() && !f.isHidden());

        AtomicInteger counter = new AtomicInteger(0);
        new MarkingScriptBuilder()
            .submissions(submissions)
            .markingScheme(MarkingScheme.class)
            .hardResetMarkingSchemeBetweenSubmissions(false)
            .beforeMarkingEachProjectDo(f -> {
                int c = counter.incrementAndGet();
                if (c==2 && MarkingScheme.CACHE==null) {
                    fail("CACHE should not be reset to null");
                }
            })
            .reportTo(f -> new ConsoleReporter())
            .run();
    }
}
