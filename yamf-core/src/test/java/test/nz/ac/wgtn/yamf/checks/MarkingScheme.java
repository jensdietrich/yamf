package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.Marking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkingScheme {

    static String CACHE = null;

    @Test
    @Marking(name = "foo",marks = 1.0)
    public void test () {
        CACHE = "value";
        assertTrue(true);
    }
}
