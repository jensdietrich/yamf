package test.nz.ac.wgtn.yamf.checks.mvn;

import nz.ac.wgtn.yamf.checks.mvn.MVNDependency;
import nz.ac.wgtn.yamf.checks.mvn.MVNChecks;
import java.io.File;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PomChecksTest {

    @Test
    public void testCheckArtifactId1() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        MVNChecks.assertValidArtifactId(file, id -> id.equals("foo"));
    }
    @Test
    public void testCheckArtifactId2() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        assertThrows(
            AssertionError.class,
            () -> MVNChecks.assertValidArtifactId(file, id -> id.equals("bar"))
        );
    }

    @Test
    public void testCheckGroupId1() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        MVNChecks.assertValidGroupId(file, id -> id.equals("nz.ac.vuw"));
    }

    @Test
    public void testCheckGroupId2() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        assertThrows(
            AssertionError.class,
            () -> MVNChecks.assertValidGroupId(file, id -> id.equals("wrong id"))
        );
    }

    @Test
    public void testCheckAllDependenciesInList() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        MVNChecks.assertValidDependencies(file, deps -> {
            if (deps.size()!=1) {
                return false;
            }
            else {
                MVNDependency dep = deps.get(0);
                String id = dep.getArtifactId();
                return id.equals("junit");
            }
        });
    }

    @Test
    public void testIsPOM1() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        MVNChecks.assertIsPOM(file);
    }

    @Test
    public void testIsPOM2() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom-invalid-xml.xml");
        assertThrows(AssertionError.class, () -> MVNChecks.assertIsPOM(file));
    }

    @Test
    public void testIsPOM3() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom1.xml");
        assertTrue(MVNChecks.isPOM(file));
    }

    @Test
    public void testIsPOM4() throws Exception {
        File file = Utils.getResourceAsFile("mvn/pom-invalid-xml.xml");
        assertFalse(MVNChecks.isPOM(file));
    }

}
