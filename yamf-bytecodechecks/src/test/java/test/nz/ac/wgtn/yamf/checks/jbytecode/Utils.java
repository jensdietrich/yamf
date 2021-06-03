package test.nz.ac.wgtn.yamf.checks.jbytecode;

import java.io.File;

public class Utils {

    static File getResourceAsFile(String name) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        return new File(classLoader.getResource(name).getFile());
    }
}
