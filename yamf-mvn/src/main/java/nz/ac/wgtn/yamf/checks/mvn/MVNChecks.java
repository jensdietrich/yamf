package nz.ac.wgtn.yamf.checks.mvn;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.Attachments;
import nz.ac.wgtn.yamf.commons.XML;
import org.junit.jupiter.api.Assertions;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Common checks for Maven POMs.
 * @author jens dietrich
 */
public class MVNChecks {

    public static boolean isPOM(File file) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(file.exists(),"Cannot check whether file is POM, file does not exist: " + file.getAbsolutePath());
        try {
            Source xmlInput=new StreamSource(new FileReader(file));
            Source schemaInput = new StreamSource(MVNChecks.class.getClassLoader().getResourceAsStream("mvn/pom-4.0.0.xsd"));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaInput);
            Validator validator = schema.newValidator();
            validator.validate(xmlInput);
            return true;
        }
        catch (SAXException e) {
            return false;
        }
    }

    public static void assertIsPOM(File file) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(file.exists(),"Cannot check whether file is POM, file does not exist: " + file.getAbsolutePath());
        try {
            Source xmlInput=new StreamSource(new FileReader(file));
            Source schemaInput = new StreamSource(MVNChecks.class.getClassLoader().getResourceAsStream("mvn/pom-4.0.0.xsd"));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaInput);
            Validator validator = schema.newValidator();
            validator.validate(xmlInput);
            Assertions.assertTrue(true);
        }
        catch (SAXException e) {
            Assertions.fail("This is not a valid POM: " + file.getAbsolutePath(),e);
        }
    }

    public static void assertValidGroupId(File pom, Predicate<String> test) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(pom.exists(),"Cannot check whether file is POM, file does not exist: " + pom.getAbsolutePath());
        NodeList nodeList = XML.evalXPath(pom, "/project/groupId");
        if (nodeList.getLength() != 1) {
            Assertions.fail("No /project/groupId element found in file " + pom.getAbsolutePath());
        }
        else {
            Node node = nodeList.item(0);
            String value = node.getTextContent();
            assert value != null;
            if (test.test(value)) {
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("the groupId \"" + value + "\" is not valid");
            }
        }
    }

    public static void assertValidArtifactId(File pom, Predicate<String> test) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(pom.exists(),"Cannot check whether file is POM, file does not exist: " + pom.getAbsolutePath());
        NodeList nodeList = XML.evalXPath(pom, "/project/artifactId");
        if (nodeList.getLength() != 1) {
            Assertions.fail("No /project/artifactId element found in file " + pom.getAbsolutePath());
        }
        else {
            Node node = nodeList.item(0);
            String value = node.getTextContent();
            assert value != null;
            if (test.test(value)) {
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("the artifactId \"" + value + "\" is not valid");
            }
        }
    }

    public static void assertConfiguresJavaVersion(File pom, Predicate<String> versionTest) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(pom.exists(),"Cannot check whether file is POM, file does not exist: " + pom.getAbsolutePath());

        // there are two ways to define version constraints, see https://mkyong.com/maven/how-to-tell-maven-to-use-java-8/
        String targetVersion = XML.evalXPathSingleNode(pom, "/project/properties/maven.compiler.target");
        String sourceVersion = XML.evalXPathSingleNode(pom, "/project/properties/maven.compiler.source");
        if (sourceVersion!=null && targetVersion!=null) {
            Assertions.assertTrue(versionTest.test(targetVersion) && versionTest.test(sourceVersion), "Version constraints (sourceVersion="+sourceVersion+", targetVersion="+targetVersion+") do not satisfy condition");
            return;
        }

        targetVersion = XML.evalXPathSingleNode(pom, "/project/build/plugins/plugin[artifactId='maven-compiler-plugin' and groupId='org.apache.maven.plugins']/configuration/target");
        sourceVersion = XML.evalXPathSingleNode(pom, "/project/build/plugins/plugin[artifactId='maven-compiler-plugin' and groupId='org.apache.maven.plugins']/configuration/source");
        if (sourceVersion!=null && targetVersion!=null) {
            Assertions.assertTrue(versionTest.test(targetVersion) && versionTest.test(sourceVersion), "Version constraints (sourceVersion="+sourceVersion+", targetVersion="+targetVersion+") do not satisfy condition");
            return;
        }

        Assertions.fail("No source and target Java version specification found in " + pom.getAbsolutePath());

    }

    public static void assertHasBuildPluginSuchThat(File pom, Predicate<POMPlugin> test) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/build/plugins/plugin");
        List<POMPlugin> plugins = POMPlugin.from(nodeList);
        for (POMPlugin plugin:plugins) {
            if (test.test(plugin)) {
                Assertions.assertTrue(true);
                return;
            }
        }
        Assertions.fail("no build plugin in the pom satisfies the requirement(s)");
    }

    public static void assertHasReportingPluginSuchThat(File pom, Predicate<POMPlugin> test) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/reporting/plugins/plugin");
        List<POMPlugin> plugins = POMPlugin.from(nodeList);
        for (POMPlugin plugin:plugins) {
            if (test.test(plugin)) {
                Assertions.assertTrue(true);
                return;
            }
        }
        Assertions.fail("no reporting plugin in the pom satisfies the requirement(s)");
    }

    public static void assertValidDependencies(File pom, Predicate<List<MVNDependency>> test) throws Exception {
        NodeList nodeList = XML.evalXPath(pom, "/project/dependencies/dependency");
        List<MVNDependency> deps = MVNDependency.from(nodeList);
        if (test.test(deps)) {
            Assertions.assertTrue(true);
        }
        else {
            Assertions.fail("the dependencies in the pom do not satisfy the requirement(s)");
        }
    }

    public static void assertHasDependencySuchThat(File pom, Predicate<MVNDependency> test) throws Exception {
        assertValidDependencies(pom, dependencies -> {
            for (MVNDependency dependency:dependencies) {
                if (test.test(dependency)) {
                    return true;
                }
            }
            Assertions.fail("No dependency found satisfing this condition");
            return false;
        });
    }

    public static void assertHasFolder (File projectFolder,String relFolderName) throws Exception {
        // note that clients should make similar precondition check using org.junit.jupiter.api.Assumptions::assume* methods !
        Preconditions.checkArgument(projectFolder.exists(),"Cannot check whether file is a mvn project folder, file does not exist: " + projectFolder.getAbsolutePath());
        Preconditions.checkArgument(projectFolder.isDirectory(),"Cannot check whether file is a mvn project folder, file is not a directory " + projectFolder.getAbsolutePath());
        File srcFolder = new File(projectFolder,relFolderName);
        Assertions.assertTrue(srcFolder.exists() && srcFolder.isDirectory(),"No source folder " + relFolderName + " found in project");
    }

    public static void assertHasSourceFolder (File projectFolder) throws Exception {
        assertHasFolder(projectFolder,"src/main/java");
    }

    public static void assertHasNonEmptySourceFolder (File projectFolder) throws Exception {
        assertHasSourceFolder(projectFolder);
        boolean hasSources = Files.walk(new File(projectFolder,"src/main/java").toPath())
            .anyMatch(p -> p.toString().endsWith(".java"));
        Assertions.assertTrue(hasSources,"No /java files found in src/main/java");
    }

    public static void assertHasResourceFolder (File projectFolder) throws Exception {
        assertHasFolder(projectFolder,"src/main/resources");
    }


    public static void assertHasTestSourceFolder (File projectFolder) throws Exception {
        assertHasFolder(projectFolder,"src/test/java");
    }

    public static void assertHasNonEmptyTestSourceFolder (File projectFolder) throws Exception {
        assertHasTestSourceFolder(projectFolder);
        boolean hasSources = Files.walk(new File(projectFolder,"src/test/java").toPath())
            .anyMatch(p -> p.toString().endsWith(".java"));
        Assertions.assertTrue(hasSources,"No /java files found in src/test/java");
    }

    public static void assertHasTestResourceFolder (File projectFolder) throws Exception {
        assertHasFolder(projectFolder,"src/test/resources");
    }

    public static void assertHasNoTestsSuchThat (File projectFolder,String status) throws Exception {
        File targetFolder = new File(projectFolder,"target");
        Preconditions.checkArgument(targetFolder.exists(),"Maven target folder does not exist, project must be build first with mvn test: " + targetFolder.getAbsolutePath());
        File surefireFolder = new File(targetFolder,"surefire-reports");
        Preconditions.checkArgument(surefireFolder.exists(),"Maven target/surefire-reports folder does not exist, project must be build first with mvn test: " + targetFolder.getAbsolutePath());

        int failureCounter = 0;
        List<String> reports = new ArrayList<>();
        for (File xmlReport:surefireFolder.listFiles( (d,f) -> f.startsWith("TEST-") && f.endsWith(".xml"))) {
            NodeList nodes = XML.evalXPath(xmlReport, "/testsuite/@" + status);
            if (nodes.getLength()==1){ // this is the root
                int counter = Integer.parseInt(nodes.item(0).getTextContent());
                failureCounter = failureCounter + counter;
                if (counter>0) {
                    reports.add("target/surefire-reports/"+xmlReport.getName());
                }
            }
            Attachment attachment = new Attachment(xmlReport.getName(),xmlReport,"application/xml");
            Attachments.add(attachment);
        }
        Assertions.assertTrue(failureCounter==0,"There are tests with status \"" + status + "\", see reports in " + reports.stream().collect(Collectors.joining()));
    }

    public static void assertHasNoFailingTests (File projectFolder) throws Exception {
        assertHasNoTestsSuchThat (projectFolder,"failures");
    }

    public static void assertHasNoErroneousTests (File projectFolder) throws Exception {
        assertHasNoTestsSuchThat (projectFolder,"errors");
    }

    public static void assertHasNoSkippedTests (File projectFolder) throws Exception {
        assertHasNoTestsSuchThat (projectFolder,"skipped");
    }
}
