package nz.ac.wgtn.yamf.checks.jee;

import nz.ac.wgtn.yamf.checks.jbytecode.JByteCodeChecks;
import nz.ac.wgtn.yamf.checks.jbytecode.JClass;

/*
 * JEE checks.
 * @author jens dietrich
 */
public class JEEChecks {

    public static void assertIsServlet(JClass clazz) {
        // TODO do full hierarchy analysis once this is implemented in bytecodechecks module
        JByteCodeChecks.assertDirectlyExtendsSuperClass(clazz, "javax.servlet.http.HttpServlet");
    }

    public static void assertImplementsDoGet(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doGet");
    }

    public static void assertImplementsDoPost(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doPost");
    }

    public static void assertImplementsDoDelete(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doDelete");
    }

    public static void assertImplementsDoPut(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doPut");
    }

    public static void assertImplementsDoConnect(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doConnect");
    }

    public static void assertImplementsDoHead(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doHead");
    }

    public static void assertImplementsDoOptions(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doOptions");
    }

    public static void assertImplementsDoTrace(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doTrace");
    }

    public static void assertImplementsDoPatch(JClass clazz) {
        assertImplementsHttpHandler(clazz,"doPatch");
    }

    private static void assertImplementsHttpHandler(JClass clazz, String methodName) {
        JByteCodeChecks.assertHasAnyMethodSuchThat(clazz,
            method -> (
                (method.isPublic() || method.isProtected())
                        && method.getName().equals(methodName)
                        && method.getParameterTypes().size()==2
                        && method.getParameterTypes().get(0).equals("javax.servlet.http.HttpServletRequest")
                        && method.getParameterTypes().get(1).equals("javax.servlet.http.HttpServletResponse")
            ), "class should contain public or protected method void " + methodName + "(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)"
        );
    }


}
