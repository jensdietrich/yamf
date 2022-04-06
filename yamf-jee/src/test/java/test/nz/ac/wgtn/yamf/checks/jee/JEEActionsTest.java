package test.nz.ac.wgtn.yamf.checks.jee;

import nz.ac.wgtn.yamf.ExpectationChecker;
import nz.ac.wgtn.yamf.checks.jbytecode.JByteCodeActions;
import nz.ac.wgtn.yamf.checks.jbytecode.JClass;
import nz.ac.wgtn.yamf.checks.jee.JEEActions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.net.URL;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JEEActionsTest {

    @Test
    public void testGetServletToURLMappingsFromWebXML() throws Exception {
        URL url = JEEActionsTest.class.getResource("/example1/src/main/webapp/WEB-INF/web.xml");
        Assumptions.assumeTrue(url!=null);
        File webxml = new File(url.getFile());
        Assumptions.assumeTrue(webxml.exists());
        Map<String,String> urlMappingsByServlet = JEEActions.getServletToURLMappingsFromWebXML(webxml);
        assertEquals(urlMappingsByServlet.get("example1.MainServlet"),"/path1,/path2");
        assertNull(urlMappingsByServlet.get("foo"));
    }

    @Test
    public void testGetServletToURLMappingsFromAnnotation() throws Exception {
        URL url = JEEActionsTest.class.getResource("/example2/target/classes/example2/MainServlet.class");
        Assumptions.assumeTrue(url!=null,"must build example2 first");
        File classFile = new File(url.getFile());
        JClass clazz = JByteCodeActions.getClass(classFile, ExpectationChecker.AssumeTrue);
        Map<String,String> urlMappingsByServlet = JEEActions.getServletToURLMappingsFromAnnotations(clazz);
        assertEquals(urlMappingsByServlet.get("example2.MainServlet"),"/path1,/path2");
        assertNull(urlMappingsByServlet.get("foo"));
    }
}
