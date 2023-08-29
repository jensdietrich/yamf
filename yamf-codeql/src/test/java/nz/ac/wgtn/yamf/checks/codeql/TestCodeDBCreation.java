package nz.ac.wgtn.yamf.checks.codeql;

import nz.ac.wgtn.yamf.Attachments;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCodeDBCreation {

    public static File TEST_PROJECT = null;
    public static boolean CODEQL_AVAILABLE = false;

    static {
        TEST_PROJECT = new File("src/test/resources/mvn-project1");
    }

    @BeforeAll
    public static void resetProject() throws Exception {
        Attachments.setTestTestMode();
        File codeqlDb = CodeQLActions.getCodeqlDb(TEST_PROJECT);
        if (codeqlDb.exists()) {
            FileUtils.deleteDirectory(codeqlDb);
        }
        CODEQL_AVAILABLE = CodeQLActions.checkCodeQLAvailability();
        System.out.println("codeql available: " + CODEQL_AVAILABLE);
    }

    @Test
    public void test1() throws Exception {
        File TEST_PROJECT_CODEQL_DB = CodeQLActions.getCodeqlDb(TEST_PROJECT);
        Assumptions.assumeTrue(CODEQL_AVAILABLE);
        Assumptions.assumeFalse(TEST_PROJECT_CODEQL_DB.exists());
        CodeQLActions.createDatabaseForMvnProject(TEST_PROJECT);
        assertTrue(TEST_PROJECT_CODEQL_DB.exists());
    }
}
