package nz.ac.wgtn.yamf.checks.cha;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Some simple actions.
 * @author jens dietrich
 */
public class JarActions {

    /**
     * Extract the main class entry from the manifest, or null if there is none set.
     * @param executableJar
     * @return
     * @throws Exception
     */
    public static String getMainClass(File executableJar) throws Exception {
        JarInputStream jarStream = new JarInputStream(new FileInputStream(executableJar));
        Manifest mf = jarStream.getManifest();
        return mf.getMainAttributes().getValue("Main-Class");
    }

}
