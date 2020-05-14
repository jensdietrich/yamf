package test.nz.ac.vuw.ecs.examples.mvn;

import nz.ac.vuw.ecs.examples.mvn.Math;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestMath {

    @Test
    public void testPlus1 () {
        assertEquals(4, Math.plus(2,2));
    }

    @Test
    public void testPlus2 () {
        assertEquals(4, Math.plus(1,3));
    }

    @Test
    public void testMinus1 () {
        assertEquals(2, Math.plus(4,2));
    }

    @Test
    public void testMinus2 () {
        assertEquals(-1, Math.plus(4,5));
    }
}
