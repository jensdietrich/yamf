package test.nz.ac.wgtn.yamf.checks.coverage;

import nz.ac.wgtn.yamf.checks.coverage.Coverage;
import nz.ac.wgtn.yamf.checks.coverage.CoverageType;
import nz.ac.wgtn.yamf.checks.coverage.JacocoActions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JacocoActionsTest {

    private Map<String, Map<CoverageType, Coverage>> coverageData = null;

    @BeforeEach
    public void setup () throws IOException {
        File jacocoReport = new File(JacocoActionsTest.class.getResource("/jacoco.csv").getFile());
        coverageData = JacocoActions.readCoverage(jacocoReport);
    }

    @AfterEach
    public void tearDown () {
        this.coverageData = null;
    }

    @Test
    public void testInstructionCoverage1 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class1");
        assertNotNull(coverage);
        assertEquals(5,coverage.get(CoverageType.INSTRUCTION).getCoveredCount());
        assertEquals(3,coverage.get(CoverageType.INSTRUCTION).getUncoveredCount());
    }

    @Test
    public void testInstructionCoverage2 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class2");
        assertNotNull(coverage);
        assertEquals(39,coverage.get(CoverageType.INSTRUCTION).getCoveredCount());
        assertEquals(181,coverage.get(CoverageType.INSTRUCTION).getUncoveredCount());
    }

    @Test
    public void testBranchCoverage1 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class1");
        assertNotNull(coverage);
        assertEquals(0,coverage.get(CoverageType.BRANCH).getCoveredCount());
        assertEquals(0,coverage.get(CoverageType.BRANCH).getUncoveredCount());
    }

    @Test
    public void testBranchCoverage2 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class2");
        assertNotNull(coverage);
        assertEquals(0,coverage.get(CoverageType.BRANCH).getCoveredCount());
        assertEquals(20,coverage.get(CoverageType.BRANCH).getUncoveredCount());
    }

    @Test
    public void testLineCoverage1 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class1");
        assertNotNull(coverage);
        assertEquals(2,coverage.get(CoverageType.LINE).getCoveredCount());
        assertEquals(1,coverage.get(CoverageType.LINE).getUncoveredCount());
    }

    @Test
    public void testLineCoverage2 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class2");
        assertNotNull(coverage);
        assertEquals(14,coverage.get(CoverageType.LINE).getCoveredCount());
        assertEquals(36,coverage.get(CoverageType.LINE).getUncoveredCount());
    }

    @Test
    public void testComplexityCoverage1 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class1");
        assertNotNull(coverage);
        assertEquals(2,coverage.get(CoverageType.COMPLEXITY).getCoveredCount());
        assertEquals(1,coverage.get(CoverageType.COMPLEXITY).getUncoveredCount());
    }

    @Test
    public void testComplexityCoverage2 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class2");
        assertNotNull(coverage);
        assertEquals(6,coverage.get(CoverageType.COMPLEXITY).getCoveredCount());
        assertEquals(22,coverage.get(CoverageType.COMPLEXITY).getUncoveredCount());
    }

    @Test
    public void testMethodCoverage1 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class1");
        assertNotNull(coverage);
        assertEquals(2,coverage.get(CoverageType.METHOD).getCoveredCount());
        assertEquals(1,coverage.get(CoverageType.METHOD).getUncoveredCount());
    }

    @Test
    public void testMethodCoverage2 () {
        Map<CoverageType, Coverage> coverage = coverageData.get("com.example.Class2");
        assertNotNull(coverage);
        assertEquals(6,coverage.get(CoverageType.METHOD).getCoveredCount());
        assertEquals(12,coverage.get(CoverageType.METHOD).getUncoveredCount());
    }
}
