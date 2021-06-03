package test.nz.ac.wgtn.yamf.checks;


import nz.ac.wgtn.yamf.commons.CachedChecks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.File;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedChecksTest {

    public static File RESOURCE_FOLDER = new File(CachedChecksTest.class.getResource("/").getFile());

    @BeforeEach
    public void before() {
        new File(RESOURCE_FOLDER,CachedChecks.CACHE_FILE_NAME).delete();
    }

    @AfterEach
    public void after() {
        new File(RESOURCE_FOLDER,CachedChecks.CACHE_FILE_NAME).delete();
    }

    @Test
    public void test1() {
        Supplier<Boolean> check = Mockito.mock(Supplier.class);
        Mockito.when(check.get()).thenReturn(true,false);
        assertTrue(CachedChecks.check(RESOURCE_FOLDER,"foo",check));
        assertTrue(CachedChecks.check(RESOURCE_FOLDER,"foo",check));
    }

    @Test
    public void test2() {
        Supplier<Boolean> check = Mockito.mock(Supplier.class);
        Mockito.when(check.get()).thenReturn(false,true);
        assertFalse(CachedChecks.check(RESOURCE_FOLDER,"foo",check));
        assertFalse(CachedChecks.check(RESOURCE_FOLDER,"foo",check));
    }
}
