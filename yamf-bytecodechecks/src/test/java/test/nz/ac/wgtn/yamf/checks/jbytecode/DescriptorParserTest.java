package test.nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.jbytecode.descr.MethodDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptorParserTest {

    @Test
    public void testMethodDescriptor1() {
        MethodDescriptor descr = DescriptorParser.parseMethodDescriptor("()I");
        assertEquals("int",descr.getReturnType());
        assertTrue(descr.getParamTypes().isEmpty());
    }

    @Test
    public void testMethodDescriptor2() {
        MethodDescriptor descr = DescriptorParser.parseMethodDescriptor("(I)V");
        assertEquals("void",descr.getReturnType());
        assertEquals(1,descr.getParamTypes().size());
        assertEquals("int",descr.getParamTypes().get(0));
    }

    @Test
    public void testMethodDescriptor3() {
        MethodDescriptor descr = DescriptorParser.parseMethodDescriptor("(ILjava/lang/String;JLjava/lang/Object;)V");
        assertEquals("void",descr.getReturnType());
        assertEquals(4,descr.getParamTypes().size());
        assertEquals("int",descr.getParamTypes().get(0));
        assertEquals("java.lang.String",descr.getParamTypes().get(1));
        assertEquals("long",descr.getParamTypes().get(2));
        assertEquals("java.lang.Object",descr.getParamTypes().get(3));
    }

    @Test
    public void testMethodDescriptor4() {
        MethodDescriptor descr = DescriptorParser.parseMethodDescriptor("([I[Ljava/lang/String;[J[Ljava/lang/Object;)[[I");
        assertEquals("int[][]",descr.getReturnType());
        assertEquals(4,descr.getParamTypes().size());
        assertEquals("int[]",descr.getParamTypes().get(0));
        assertEquals("java.lang.String[]",descr.getParamTypes().get(1));
        assertEquals("long[]",descr.getParamTypes().get(2));
        assertEquals("java.lang.Object[]",descr.getParamTypes().get(3));
    }

    @Test
    public void testMethodDescriptor5() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> DescriptorParser.parseMethodDescriptor("(I)this is not a valid descriptor, even the start looks like one")
        );
    }

    @Test
    public void testFieldDescriptor1() {
        String descr = DescriptorParser.parseFieldDescriptor("I");
        assertEquals("int",descr);
    }

    @Test
    public void testFieldDescriptor2() {
        String descr = DescriptorParser.parseFieldDescriptor("[Z");
        assertEquals("boolean[]",descr);
    }

    @Test
    public void testFieldDescriptor3() {
        String descr = DescriptorParser.parseFieldDescriptor("[[B");
        assertEquals("byte[][]",descr);
    }

    @Test
    public void testFieldDescriptor4() {
        String descr = DescriptorParser.parseFieldDescriptor("Ljava/lang/String;");
        assertEquals("java.lang.String",descr);
    }

    @Test
    public void testFieldDescriptor5() {
        String descr = DescriptorParser.parseFieldDescriptor("[Ljava/lang/String;");
        assertEquals("java.lang.String[]",descr);
    }

    @Test
    public void testFieldDescriptor6() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> DescriptorParser.parseFieldDescriptor("II")
        );
    }

    @Test
    public void testFieldDescriptor7() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DescriptorParser.parseFieldDescriptor("void")
        );
    }

}
