package nz.ac.wgtn.yamf.commons;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility to check for the presence of IDE meta data in submissions.
 * @author jens dietrich
 */
public class IDE {

    /**
     * Extract the names of eclipse-specific files and folder. Only local names will be reported.
     * @param projectFolder list of folder names
     * @return
     */
    public static @Nonnull List<String> getEclipseProjectFiles(File projectFolder) {
        List<String> list = new ArrayList<>(3);
        checkAndAdd(list,projectFolder,".classpath");
        checkAndAdd(list,projectFolder,".project");
        checkAndAdd(list,projectFolder,".settings");
        return Collections.unmodifiableList(list);
    }

    /**
     * Extract the names of intellij-specific files and folder. Only local names will be reported.
     * @param projectFolder list of folder names
     * @return
     */
    public static @Nonnull List<String> getIntellijProjectFiles(File projectFolder) {
        List<String> list = new ArrayList<>(1);
        checkAndAdd(list,projectFolder,".idea");
        return Collections.unmodifiableList(list);
    }

    /**
     * Extract the names of intellij-specific files and folder. Only local names will be reported.
     * @param projectFolder list of folder names
     * @return
     */
    public static @Nonnull List<String> getIDEProjectFiles(File projectFolder) {
        List<String> list = new ArrayList<>();
        list.addAll(getEclipseProjectFiles(projectFolder));
        list.addAll(getIntellijProjectFiles(projectFolder));
        return Collections.unmodifiableList(list);
    }


    private static void checkAndAdd(List<String> list, File projectFolder, String fileName) {
        if (new File(projectFolder,fileName).exists()) {
            list.add(fileName);
        }
    }
}
