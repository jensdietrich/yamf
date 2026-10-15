package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.ImportMark;
import nz.ac.wgtn.yamf.ManualMarkingIsRequired;
import nz.ac.wgtn.yamf.Marking;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiverseMarkingScheme {


    static boolean importedExecuted = false;

    public static double importer (String context) {
        if (!context.equals("test4")) throw new IllegalArgumentException();
        importedExecuted = true;
        return 0.5;
    }

    @Test
    @Marking(name = "foo1",marks = 1.0)
    public void test1 () {
        assertTrue(true);
    }

    @Test
    @Marking(name = "foo2",marks = 1.0)
    public void test2 () {
        assertTrue(false);
    }

    @Test
    @Marking(name="foo3", marks = 1.0)
    @ManualMarkingIsRequired
    public void test3 () {}

    @Test
    @Marking(name="foo4", marks = 1.0)
    @ImportMark(clazz = DiverseMarkingScheme.class,method = "importer",context="test4")
    public void test4 () {}

}
