package test.nz.ac.wgtn.yamf;

import java.io.*;
import java.util.*;

public class SampleClass implements java.io.Serializable {

    private static final List<String> field1 = null;
    protected int[] field2 = null;

    private static void foo(int i,String s) throws IOException , InterruptedException {
        System.out.println("foo " + i);
    }

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