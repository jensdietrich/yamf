package test.nz.ac.wgtn.yamf;

import nz.ac.wgtn.yamf.checks.cha.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JClassTest {

    private JClass jclazz = null;

    @BeforeEach
    public void setup () throws Exception {
        URL url = JClassTest.class.getResource("/test/nz/ac/wgtn/yamf/SampleClass.class");
        System.out.println(url);
        File file = new File(url.getFile());
        System.out.println(file);
        System.out.println(file.exists());
        System.out.println();
        jclazz = JByteCodeActions.getClass(file);
    }

    @AfterEach
    public void tearDown () throws Exception {
        jclazz = null;
    }

    @Test
    public void testClassName () throws Exception {
        assertEquals("test.nz.ac.wgtn.yamf.SampleClass",jclazz.getName());
    }

    @Test
    public void testSuperClass () throws Exception {
        assertEquals("java.lang.Object",jclazz.getSuperClass());
    }

    @Test
    public void testInterfaces () throws Exception {
        assertEquals(1,jclazz.getInterfaces().size());
        assertTrue(jclazz.getInterfaces().contains("java.io.Serializable"));
    }

    @Test
    public void testFields () throws Exception {
        assertEquals(2,jclazz.getFields().size());
    }

    @Test
    public void testField1 () throws Exception {
        Optional<JField> match = jclazz.getFields().stream().filter(f -> f.getName().equals("field1")).findAny();
        assertTrue(match.isPresent());
        JField field = match.get();
        assertEquals("java.util.List",field.getDescriptor());  // erasure
        assertTrue(field.isPrivate());
        assertTrue(field.isStatic());
        assertTrue(field.isFinal());
    }

    @Test
    public void testField2 () throws Exception {
        Optional<JField> match = jclazz.getFields().stream().filter(f -> f.getName().equals("field2")).findAny();
        assertTrue(match.isPresent());
        JField field = match.get();
        assertEquals("int[]",field.getDescriptor());  // erasure
        assertTrue(field.isProtected());
        assertTrue(!field.isStatic());
        assertTrue(!field.isFinal());
    }

    @Test
    public void testConstructor () throws Exception {
        Optional<JMethod> match = jclazz.getMethods().stream().filter(m -> m.isConstructor()).findAny();
        assertTrue(match.isPresent());
        JMethod method = match.get();
        assertEquals("void",method.getReturnType());
        assertEquals(0,method.getParameterTypes().size());
        assertTrue(method.isPublic());
        assertTrue(!method.isFinal());
        assertTrue(!method.isStatic());
    }

    @Test
    public void testStaticBlock () throws Exception {
        Optional<JMethod> match = jclazz.getMethods().stream().filter(m -> m.isStaticBlock()).findAny();
        assertTrue(match.isPresent());
        JMethod method = match.get();
        assertEquals("void",method.getReturnType());
        assertEquals(0,method.getParameterTypes().size());
        assertTrue(method.isStatic());
    }


    @Test
    public void testMethod1 () throws Exception {
        Optional<JMethod> match = jclazz.getMethods().stream().filter(m -> m.getName().equals("foo")).findAny();
        assertTrue(match.isPresent());
        JMethod method = match.get();

        assertEquals("void",method.getReturnType());
        assertEquals(2,method.getParameterTypes().size());
        assertEquals("int",method.getParameterTypes().get(0));
        assertEquals("java.lang.String",method.getParameterTypes().get(1));

        assertEquals(2,method.getExceptions().size());
        assertTrue(method.getExceptions().contains("java.io.IOException"));
        assertTrue(method.getExceptions().contains("java.lang.InterruptedException"));

        Invocation inv = method.getInvocations().stream().filter(i -> i.getName().equals("println")).findAny().get();
        assertEquals("java.io.PrintStream",inv.getOwner());
        assertEquals("println",inv.getName());
        assertEquals("void",inv.getDescriptor().getReturnType());
        assertEquals(1,inv.getDescriptor().getParamTypes().size());
        assertEquals("java.lang.String",inv.getDescriptor().getParamTypes().get(0));

        assertTrue(method.isPrivate());
        assertTrue(!method.isFinal());
        assertTrue(method.isStatic());
    }

//    public long bar() throws IOException {
//        foo(42,"hello");
//        return 0;
//    }

    @Test
    public void testMethod2 () throws Exception {
        Optional<JMethod> match = jclazz.getMethods().stream().filter(m -> m.getName().equals("bar")).findAny();
        assertTrue(match.isPresent());
        JMethod method = match.get();
        assertEquals("long",method.getReturnType());
        assertEquals(0,method.getParameterTypes().size());

        assertEquals(1,method.getExceptions().size());
        assertTrue(method.getExceptions().contains("java.lang.Exception"));

        assertEquals(1,method.getInvocations().size());
        Invocation inv = method.getInvocations().iterator().next();
        assertEquals("test.nz.ac.wgtn.yamf.SampleClass",inv.getOwner());
        assertEquals("foo",inv.getName());
        assertEquals("void",inv.getDescriptor().getReturnType());
        assertEquals(2,inv.getDescriptor().getParamTypes().size());
        assertEquals("int",inv.getDescriptor().getParamTypes().get(0));
        assertEquals("java.lang.String",inv.getDescriptor().getParamTypes().get(1));

        assertTrue(method.isPublic());
        assertTrue(!method.isFinal());
        assertTrue(!method.isStatic());
    }

    // call in lambdas are not detected, this test shows this limitation
    @Disabled
    @Test
    public void testMethodWithLambda () throws Exception {
        Optional<JMethod> match = jclazz.getMethods().stream().filter(m -> m.getName().equals("doLambda")).findAny();
        assertTrue(match.isPresent());
        JMethod method = match.get();

        boolean fooInvocationDetected = method.getInvocations().stream().anyMatch(invocation -> invocation.getName().equals("foo"));
        assertTrue(fooInvocationDetected);
    }


}
