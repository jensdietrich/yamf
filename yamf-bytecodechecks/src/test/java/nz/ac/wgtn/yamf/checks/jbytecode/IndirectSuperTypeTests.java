package nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.OnFailure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


public class IndirectSuperTypeTests {

    private static Path CLASS_DIR = null;
    private static Path JAR1 = null;
    private static Path JAR2 = null;

    @BeforeAll
    public static void init() {

        String msg = "test resource does not exist, must build test ddata project with \"mvn dependency:copy-dependencies compile\" first, resource is: ";
        CLASS_DIR = Path.of(IndirectSuperTypeTests.class.getResource("/testproject4supertypeinference/target/classes/").getPath());
        assumeTrue(Files.exists(CLASS_DIR), msg+CLASS_DIR);

        JAR1 = Path.of(IndirectSuperTypeTests.class.getResource("/testproject4supertypeinference/target/dependency/log4j-api-2.25.2.jar").getPath());
        assumeTrue(Files.exists(JAR1), msg+JAR1);

        JAR2 = Path.of(IndirectSuperTypeTests.class.getResource("/testproject4supertypeinference/target/dependency/log4j-core-2.25.2.jar").getPath());
        assumeTrue(Files.exists(JAR2), msg+JAR2);
    }

    @Test
    public void test1() throws Exception {
        JClass myAppenderClass = JByteCodeActions.getClass(CLASS_DIR.resolve("foo/MyAppender.class").toFile(),OnFailure.DEFAULT);
        assumeTrue(myAppenderClass!=null);
        JByteCodeChecks.assertExtendsOrImplements(myAppenderClass,"org.apache.logging.log4j.core.appender.AbstractAppender",CLASS_DIR,JAR1,JAR2);
    }

    @Test
    public void test2() throws Exception {
        JClass myAppenderClass = JByteCodeActions.getClass(CLASS_DIR.resolve("foo/MyAppender.class").toFile(),OnFailure.DEFAULT);
        assumeTrue(myAppenderClass!=null);
        JByteCodeChecks.assertExtendsOrImplements(myAppenderClass,"org.apache.logging.log4j.core.Appender",CLASS_DIR,JAR1,JAR2);
    }

}
