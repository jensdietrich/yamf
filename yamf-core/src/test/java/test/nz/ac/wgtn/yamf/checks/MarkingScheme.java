package test.nz.ac.wgtn.yamf.checks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkingScheme {

    static String CACHE = null;

    @Test
    public void test () {
        CACHE = "value";
        assertTrue(true);
    }
}
