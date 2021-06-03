package nz.ac.wgtn.yamf.checks.mvn;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.Attachments;
import nz.ac.wgtn.yamf.checks.junit.JUnitActions;
import nz.ac.wgtn.yamf.checks.junit.JUnitVersion;
import nz.ac.wgtn.yamf.checks.junit.TestResults;
import nz.ac.wgtn.yamf.commons.Files;
import nz.ac.wgtn.yamf.commons.OS;
import nz.ac.wgtn.yamf.commons.XML;
import org.junit.jupiter.api.Assumptions;
import org.w3c.dom.NodeList;
import org.zeroturnaround.exec.ProcessResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various mvn actions.
 * @author jens dietrich
 */
public class MVNActions {
    
    private static boolean isWindows = false;
    private static boolean osChecked = false;

    public static ProcessResult mvn(File projectFolder, Properties properties, String... phases) throws Exception {
        Preconditions.checkArgument(projectFolder!=null,"Cannot run \"mvn\" -- project folder is null");
        Preconditions.checkArgument(projectFolder.exists(),"Cannot run \"mvn\" -- project folder does not exist: " + projectFolder.getAbsolutePath());
        String[] cmd = new String[phases.length+properties.size()+1 ];
        int index = 1;
        cmd[0] = getMvnString();
        for (String propertyName:properties.stringPropertyNames()) {
            cmd[index] ="-D"+propertyName+"="+properties.getProperty(propertyName);
            index = index+1;
        }
        for (int i=0;i<phases.length;i++) {
            cmd[index] = phases[i];
            index = index+1;
        }
        ProcessResult result = OS.exe(projectFolder,cmd);
        String cmdAsString = "mvn " + Stream.of(phases).collect(Collectors.joining(" "));
        Assumptions.assumeTrue(result.getExitValue()==0,"Command \"" + cmdAsString + "\" has failed " + System.lineSeparator() + result.outputString());

        return result;
    }

    public static ProcessResult mvn(File projectFolder,String... phases) throws Exception {
        return mvn(projectFolder,new Properties(),phases);
    }

    public static ProcessResult compile (File projectFolder) throws Exception {
        return mvn(projectFolder,"compile");
    }

    public static ProcessResult compileTests (File projectFolder) throws Exception {
        // do not actually run tests
        return mvn(projectFolder,"compile","compiler:testCompile");
    }

    public static ProcessResult test (File projectFolder) throws Exception {
        return test(projectFolder,true);
    }

    public static String inferClasspath(File projectFolder,boolean includeTargetFolder) throws Exception {
        File classpathFile = new File(".tmp-project-classpath.txt"); // using a file avoid parsing the (potentially changable) log string
        Properties properties = new Properties();
        properties.setProperty("mdep.outputFile",classpathFile.getAbsolutePath());
        ProcessResult result = mvn(projectFolder,properties,"dependency:build-classpath");
        BufferedReader reader = new BufferedReader(new FileReader(classpathFile));
        String classpath = reader.lines().collect(Collectors.joining());
        if (includeTargetFolder) {
            File projectClassesFolder = new File(projectFolder,"target/classes");
            classpath = projectClassesFolder.getAbsolutePath() + File.pathSeparator + classpath;
        }
        return classpath;
    }


    public static String inferClasspath(File projectFolder) throws Exception {
        return inferClasspath(projectFolder,true);
    }

    public static ProcessResult test (File projectFolder, boolean ignoreFailed) throws Exception {
        Preconditions.checkArgument(projectFolder!=null,"Cannot run \"mvn\" -- project folder is null");
        Preconditions.checkArgument(projectFolder.exists(),"Cannot run \"mvn\" -- project folder does not exist: " + projectFolder.getAbsolutePath());
        ProcessResult result = null;
        if (ignoreFailed) {
            result = OS.exe(projectFolder, getMvnString(), "-Dmaven.test.failure.ignore=true", "-Dmaven.test.error.ignore=true","test");
        }
        else {
            result = OS.exe(projectFolder, getMvnString(), "test");
        }

        // set attachments -- concatenate all txt files in target/surefire-reports
        File junitReportsFolder = new File(projectFolder,"target/surefire-reports");
        List<String> mergedContent = new ArrayList<>();
        if (junitReportsFolder.exists()) {
            Stream.of(junitReportsFolder.listFiles())
                .filter(f -> f.getName().endsWith(".txt"))
                .map(f -> new Attachment(f.getName(),f,"test/plain"))
                .forEach(attachment -> {
                    mergedContent.add("");
                    mergedContent.add("target/surefire-reports/"+attachment.getFile().getName());
                    mergedContent.addAll(attachment.getContent());
                });
            Attachment attachment = new Attachment("surefire-reports",mergedContent,"test/plain");
            Attachments.add(attachment);

        }
        Assumptions.assumeTrue(result.getExitValue()==0,"Command \"mvn test\" has failed");
        return result;
    }

    /**
     * After running tests, get the test reports from target/surefire-reports and return them as map, the key is the class name.
     * @param projectFolder
     * @param includeJUnitReports whether to include the content of the generated junit report(s) in the test results
     * @return
     * @throws Exception
     */
    public static Map<String,TestResults> getTestResults(File projectFolder,boolean includeJUnitReports) throws Exception {
        Preconditions.checkArgument(projectFolder!=null,"Cannot run \"mvn\" -- project folder is null");
        Preconditions.checkArgument(projectFolder.exists(),"Cannot run \"mvn\" -- project folder does not exist: " + projectFolder.getAbsolutePath());

        File surefireReportsFolder = new File(projectFolder,"target/surefire-reports");
        Preconditions.checkArgument(surefireReportsFolder.exists(),"Surefire reports folder does not exist: " + surefireReportsFolder.getAbsolutePath() + " -- run \"mvn test\"first");

        Map<String,TestResults> results = new HashMap<>();
        for (File reportFile:surefireReportsFolder.listFiles((d,n) -> n.startsWith("TEST-") && n.endsWith(".xml"))) {
            TestResults testResults = new TestResults();
            String testClassName = XML.evalXPathSingleNode(reportFile, "/testsuite/@name");
            int testCount = XML.evalXPathSingleNodeAsInt(reportFile, "/testsuite/@tests");
            testResults.setTests(testCount);
            int testsFailed = XML.evalXPathSingleNodeAsInt(reportFile, "/testsuite/@failures");
            testResults.setTestsFailed(testsFailed);
            int testSkippedCount = XML.evalXPathSingleNodeAsInt(reportFile, "/testsuite/@skipped");
            testResults.setTestsSkipped(testSkippedCount);
            int testResultingInErrorCounts = XML.evalXPathSingleNodeAsInt(reportFile, "/testsuite/@errors");
            testResults.setTestsWithErrors(testResultingInErrorCounts);

            // for details, use txt files
            File txtReport = new File(surefireReportsFolder,testClassName+"txt");
            String details = null;
            if (txtReport.exists()) {
                if (includeJUnitReports) {
                    try (Stream<String> stream = java.nio.file.Files.lines(txtReport.toPath())) {
                        details = stream.collect(Collectors.joining());
                    } catch (IOException e) {
                        throw new Exception(e);
                    }
                }
                else {
                    details = "junit reports were generated in " + txtReport.getAbsolutePath();
                }
            }
            testResults.setDetails(details);
            results.put(testClassName,testResults);
        }
        return results;
    }

    @Deprecated // use inferClasspath -- uses more stable file
    public static String getProjectClassPath (File projectFolder) throws Exception {
        ProcessResult result = OS.exe(projectFolder, getMvnString(),"dependency:build-classpath");
        String output = result.outputString();

        //  look for output:
        //  [INFO] --- maven-dependency-plugin:2.8:build-classpath (default-cli) @ jmkr-mvn-example2-acceptancetests ---
        //  [INFO] Dependencies classpath:
        //  /Users/foo/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.6.2/junit-jupiter-engine-5.6.2.jar:/Users/foo/.m2/repository/org/apiguardian/apiguardian-api/1.1.0/apiguardian-api-1.1.0.jar:

        String[] lines = output.split("\\r?\\n");
        assert lines.length>0;
        boolean classPathIsNext = false;
        for (String line:lines) {
            if (classPathIsNext) {
                String[] tokens = line.split(" ");
                if (tokens.length==2) {
                    line = tokens[1]; // remove logger name part
                }
                else if (tokens.length==1) {
                    line = tokens[0];
                }
                else {
                    assert tokens.length==1;
                }
                return line;
            }
            classPathIsNext = line.trim().endsWith("Dependencies classpath:");

        }
        Assumptions.assumeTrue(false,"Cannot get Maven project classpath");
        return null;
    }

    // -Dmaven.test.failure.ignore=true (or -DtestFailureIgnore=true)

    public static File getClassFile(File projectFolder,String className) {
        Preconditions.checkArgument(projectFolder.exists(),"Project folder does not exist: " + projectFolder.getAbsolutePath());
        File targetFolder = new File(projectFolder,"target");
        Preconditions.checkState(targetFolder.exists(),"Target folder does not exist (project must be built first): " + targetFolder.getAbsolutePath());
        File classesFolder = new File(targetFolder,"classes");
        Preconditions.checkState(classesFolder.exists(),"Classes folder does not exist (project must be built first with \"mvn compile\"): " + classesFolder.getAbsolutePath());
        return new File(classesFolder,className.replace(".","/") + ".class");

    }

    public static Set<File> getAllClassFiles(File projectFolder) throws IOException {
        Preconditions.checkArgument(projectFolder.exists(),"Project folder does not exist: " + projectFolder.getAbsolutePath());
        File targetFolder = new File(projectFolder,"target");
        Preconditions.checkState(targetFolder.exists(),"Target folder does not exist (project must be built first): " + targetFolder.getAbsolutePath());
        File classesFolder = new File(targetFolder,"classes");
        Preconditions.checkState(classesFolder.exists(),"Classes folder does not exist (project must be built first with \"mvn compile\"): " + classesFolder.getAbsolutePath());
        return java.nio.file.Files.walk(classesFolder.toPath())
            .filter(java.nio.file.Files::isRegularFile)
            .filter(f -> f.toFile().getName().endsWith(".class"))
            .map(f -> f.toFile())
            .collect(Collectors.toSet());
    }

    public static Set<File> getAllTestClassFiles(File projectFolder) throws IOException {
        Preconditions.checkArgument(projectFolder.exists(),"Project folder does not exist: " + projectFolder.getAbsolutePath());
        File targetFolder = new File(projectFolder,"target");
        Preconditions.checkState(targetFolder.exists(),"Target folder does not exist (project must be built first): " + targetFolder.getAbsolutePath());
        File classesFolder = new File(targetFolder,"test-classes");
        Preconditions.checkState(classesFolder.exists(),"Test classes folder does not exist (project must be built first with \"mvn compile compiler:testCompile\"): " + classesFolder.getAbsolutePath());
        return java.nio.file.Files.walk(classesFolder.toPath())
            .filter(java.nio.file.Files::isRegularFile)
            .filter(f -> f.toFile().getName().endsWith(".class"))
            .map(f -> f.toFile())
            .collect(Collectors.toSet());
    }

    public static File getTestClassFile(File projectFolder,String className) {
        Preconditions.checkArgument(projectFolder.exists(),"Project folder does not exist: " + projectFolder.getAbsolutePath());
        File targetFolder = new File(projectFolder,"target");
        Preconditions.checkState(targetFolder.exists(),"Target folder does not exist (project must be built first): " + targetFolder.getAbsolutePath());
        File classesFolder = new File(targetFolder,"test-classes");
        Preconditions.checkState(classesFolder.exists(),"Test classes folder does not exist (project must be built first with \"mvn compile compiler:testCompile\"): " + classesFolder.getAbsolutePath());
        return new File(classesFolder,className.replace(".","/") + ".class");
    }

    /**
     * Run junit tests, compose classpath from two maven projects: the project to test, and the project containing the acceptance tests.
     * @param junitRunner the junit runner library, for instance junit-platform-console-standalone-1.6.2.jar
     * @param testClass the name of the class with tests
     * @param mvnProjectToBeTestedFolder the project to be tested
     * @param mvnProjectWithAcceptanceTestsFolder the project that has the acceptance tests
     * @param buildMvn whether to run the necessary mvn builds in the projects to ensure that the binaries exist
     * @param junitVersion the junit version used by acceptance tests (to include the appropriate reports)
     */
    public static TestResults acceptanceTestMvnProject (File junitRunner, String testClass, File mvnProjectToBeTestedFolder, File mvnProjectWithAcceptanceTestsFolder, boolean buildMvn, JUnitVersion junitVersion) throws Exception {
        if (buildMvn) {
            MVNActions.compileTests(mvnProjectWithAcceptanceTestsFolder);
        }
        File acceptanceTestsClassesFolder = new File(mvnProjectWithAcceptanceTestsFolder,"target/test-classes");
        Preconditions.checkState(acceptanceTestsClassesFolder.exists(),"Folder with acceptance tests does not exist, project " + acceptanceTestsClassesFolder + " must be build first with \"mvn compile compiler:testCompile\"" );

        if (buildMvn) {
            MVNActions.compile(mvnProjectToBeTestedFolder);
        }
        File submissionClassesFolder = new File(mvnProjectToBeTestedFolder,"target/classes");
        Preconditions.checkState(submissionClassesFolder.exists(),"Folder with classes to be tested does not exist, project " + submissionClassesFolder + " must be build first with \"mvn compile\"" );

        String classpathFromMavenProject = MVNActions.getProjectClassPath(mvnProjectToBeTestedFolder);

        String classPath = classpathFromMavenProject;
        if (classPath==null || classPath.length()==0) {
            classPath = acceptanceTestsClassesFolder.getAbsolutePath()
                    + System.getProperty("path.separator") + submissionClassesFolder.getAbsolutePath();
        }
        else {
            classPath = classPath
                    + System.getProperty("path.separator") + acceptanceTestsClassesFolder.getAbsolutePath()
                    + System.getProperty("path.separator") + submissionClassesFolder.getAbsolutePath();
        }
        return JUnitActions.test(junitRunner,testClass,classPath,junitVersion);
    }


    /**
     * Run a test that is defined within the maven project.
     * @param junitRunner the junit runner library, for instance junit-platform-console-standalone-1.6.2.jar
     * @param testClass the name of the class with tests
     * @param mvnProjectToBeTestedFolder the project to be tested
     * @param buildMvn whether to run the necessary mvn builds in the projects to ensure that the binaries exist
     * @param junitVersion the junit version used by acceptance tests (to include the appropriate reports)
     */
    public static TestResults testMvnProject (File junitRunner, String testClass,File mvnProjectToBeTestedFolder, boolean buildMvn,JUnitVersion junitVersion) throws Exception {

        if (buildMvn) {
            MVNActions.compileTests(mvnProjectToBeTestedFolder);
        }
        File submissionClassesFolder = new File(mvnProjectToBeTestedFolder,"target/classes");
        Preconditions.checkState(submissionClassesFolder.exists(),"Folder with classes to be tested does not exist, project " + submissionClassesFolder + " must be build first with \"mvn compile compiler:testCompile\"" );

        String classpathFromMavenProject = MVNActions.getProjectClassPath(mvnProjectToBeTestedFolder);

        String classPath = classpathFromMavenProject;
        if (classPath==null || classPath.length()==0) {
            classPath = submissionClassesFolder.getAbsolutePath();
        }
        else {
            classPath = classPath + System.getProperty("path.separator") + submissionClassesFolder.getAbsolutePath();
        }
        return JUnitActions.test(junitRunner,testClass,classPath,junitVersion);
    }
    
    private static String getMvnString() {
        if (!osChecked) { checkOSisWindows(); }
        if (isWindows) {
            return "mvn.cmd";
        } else {
            return "mvn";
        }
    }

    private static void checkOSisWindows() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            isWindows = true;
        }
        osChecked = true;
    }

    /**
     * Get the top-most maven project (folder with pom.xml), or null if it does not exist. The top-most folder can be root.
     * @param root
     * @return
     */
    public static File getTopMostMvnProjectFolder(File root) {
        Preconditions.checkNotNull(root);
        Preconditions.checkArgument(root.exists());
        Preconditions.checkArgument(root.isDirectory());
        return Files.findTopMostChildSuchThat(root, f -> f.isDirectory() && new File(f,"pom.xml").exists());
    }

    /**
     * Get the dependencies from a pom.
     * @param pom
     * @return
     * @throws Exception
     */
    public static List<MVNDependency> getDependencies(File pom) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/dependencies/dependency");
        return MVNDependency.from(nodeList);
    }

    /**
     * Get the plugins from a pom.
     * @param pom
     * @return
     * @throws Exception
     */
    public static List<POMPlugin> getPlugins(File pom) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "//plugins/plugin");
        return POMPlugin.from(nodeList);
    }

    /**
     * Get the reporting plugins from a pom.
     * @param pom
     * @return
     * @throws Exception
     */
    public static List<POMPlugin> getReportingPlugins(File pom) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/reporting/plugins/plugin");
        return POMPlugin.from(nodeList);
    }

    /**
     * Get the build plugins from a pom.
     * @param pom
     * @return
     * @throws Exception
     */
    public static List<POMPlugin> getBuildPlugins(File pom) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/build/plugins/plugin");
        return POMPlugin.from(nodeList);
    }

}
