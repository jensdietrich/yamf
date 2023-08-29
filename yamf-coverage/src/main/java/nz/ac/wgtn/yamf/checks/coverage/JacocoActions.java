package nz.ac.wgtn.yamf.checks.coverage;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.OnFailure;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Jacoco - related actions.
 * @author jens dietrich
 */
public class JacocoActions {

    /**
     * Extract a map containing coverage data by class.
     * Only the csv format (coverage.csv) is currently supported.
     * @author jens dietrich
     */
    public static Map<String,Map<CoverageType,Coverage>> readCoverage(File jacocoCSVReport) throws IOException {
        Preconditions.checkArgument(jacocoCSVReport!=null);
        Preconditions.checkArgument(jacocoCSVReport.exists());

        Map<String,Map<CoverageType,Coverage>> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(jacocoCSVReport))) {
            String header = reader.readLine();
            assert header!=null;
            assert header.startsWith("GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED");
            String line;
            while ((line=reader.readLine())!=null) {
                String [] tokens = line.split(",");
                Preconditions.checkArgument(tokens.length>=13);
                String className = tokens[1]+'.'+tokens[2];
                Map<CoverageType,Coverage> coverageMap = new HashMap<>();
                map.put(className,coverageMap);
                coverageMap.put(CoverageType.INSTRUCTION,new Coverage(className,Integer.parseInt(tokens[4]),Integer.parseInt(tokens[3]),CoverageType.INSTRUCTION));
                coverageMap.put(CoverageType.BRANCH,new Coverage(className,Integer.parseInt(tokens[6]),Integer.parseInt(tokens[5]),CoverageType.BRANCH));
                coverageMap.put(CoverageType.LINE,new Coverage(className,Integer.parseInt(tokens[8]),Integer.parseInt(tokens[7]),CoverageType.LINE));
                coverageMap.put(CoverageType.COMPLEXITY,new Coverage(className,Integer.parseInt(tokens[10]),Integer.parseInt(tokens[9]),CoverageType.COMPLEXITY));
                coverageMap.put(CoverageType.METHOD,new Coverage(className,Integer.parseInt(tokens[12]),Integer.parseInt(tokens[11]),CoverageType.METHOD));
            }
        }
        return map;
    }

    public static double getCoverage(File coverageCSVReport, String className, CoverageType coverageType, OnFailure failureHandler) {
        failureHandler.check(coverageCSVReport!=null,"jacoco coverage report does not exist: " + coverageCSVReport.getAbsolutePath());
        failureHandler.check(coverageCSVReport.exists(),"jacoco coverage report does not exist: " + coverageCSVReport.getAbsolutePath());
        try {
            Map<String, Map<CoverageType, Coverage>> coverageMap = JacocoActions.readCoverage(coverageCSVReport);
            Map<CoverageType, Coverage> coverageOfClass = coverageMap.get(className);
            failureHandler.check(coverageOfClass != null, "No coverage information found for class " + className);
            Coverage coverage = coverageOfClass.get(coverageType);
            failureHandler.check(coverage != null, "No coverage information found for class " + className + " and coverage type " + coverageType);
            return coverage.getValue();
        }
        catch (Exception x) {
            failureHandler.handle(x,"Error parsing jacoco report");
            return 0;
        }
    }

    public static double getInstructionCoverage(File coverageCSVReport, String className,OnFailure failureHandler) {
        return getCoverage(coverageCSVReport,className,CoverageType.INSTRUCTION,failureHandler);
    }

    public static double getBranchCoverage(File coverageCSVReport, String className,OnFailure failureHandler) {
        return getCoverage(coverageCSVReport,className,CoverageType.BRANCH,failureHandler);
    }

    public static double getLineCoverage(File coverageCSVReport, String className,OnFailure failureHandler) {
        return getCoverage(coverageCSVReport,className,CoverageType.LINE,failureHandler);
    }

}
