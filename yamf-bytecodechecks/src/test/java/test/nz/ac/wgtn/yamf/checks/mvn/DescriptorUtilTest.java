package test.nz.ac.wgtn.yamf.checks.mvn;

import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptorUtilTest {

    @Test
    public void test1() {
        assertEquals("I", DescriptorUtil.convertTypeNameToInternal("int"));
    }

    @Test
    public void test2() {
        assertEquals("Ljava/lang/String;", DescriptorUtil.convertTypeNameToInternal("java.lang.String"));
    }

    @Test
    public void test3() {
        assertEquals("[I", DescriptorUtil.convertTypeNameToInternal("[int"));
    }

    @Test
    public void test4() {
        assertEquals("[Ljava/lang/Object;", DescriptorUtil.convertTypeNameToInternal("[java.lang.Object"));
    }



    // (Ljava/util/Locale;Ljava/lang/String;L[Ljava/lang/Object;;)
}
