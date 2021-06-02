package test.nz.ac.wgtn.yamf.checks.mvn;

import nz.ac.wgtn.yamf.checks.jbytecode.JClass;
import nz.ac.wgtn.yamf.checks.jbytecode.ReflectionActions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectiveActionsTest {

    @Test
    public void test1() throws Exception {
        JClass clazz = ReflectionActions.getClass("java.io.PrintStream");
        assertTrue(clazz!=null);

    }
}
