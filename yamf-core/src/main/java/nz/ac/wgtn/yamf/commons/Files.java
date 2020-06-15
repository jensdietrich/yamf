package nz.ac.wgtn.yamf.commons;

import java.io.File;
import java.nio.file.Path;

/**
 * File related utilities.
 * @author jens dietrich
 */
public class Files {
    public static String relativize(File file, File baseFolder) {
        Path pathAbsolute = file.toPath();
        Path pathBase = baseFolder.toPath();
        return pathBase.relativize(pathAbsolute).toString();
    }

}
