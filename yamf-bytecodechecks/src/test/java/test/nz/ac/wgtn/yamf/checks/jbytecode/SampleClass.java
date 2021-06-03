package test.nz.ac.wgtn.yamf.checks.jbytecode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.*;
import java.util.*;


@DisplayName("some-class")
public class SampleClass implements java.io.Serializable {

    private static final List<String> field1 = null;
    protected int[] field2 = null;

    @DisplayName("some-method")
    private static void foo(int i,String s) throws IOException , InterruptedException {
        System.out.println("foo " + i);
    }

    @ValueSource(strings = { "a", "b" })
    public long bar() throws Exception {
        foo(42,"hello");
        return 0;
    }

    public void doLambda() throws Exception {
        java.util.Arrays.stream(new String[]{"s"}).forEach(s -> {
            try {
                foo(42,s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {}
}