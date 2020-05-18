package nz.ac.wgtn.yamf.commons;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;

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
    public static @Nonnull Set<String> getEclipseProjectFiles(File projectFolder) {
        Set<String> set = new HashSet<>();
        checkAndAdd(set,projectFolder,".classpath");
        checkAndAdd(set,projectFolder,".project");
        checkAndAdd(set,projectFolder,".settings");
        return Collections.unmodifiableSet(set);
    }

    /**
     * Extract the names of intellij-specific files and folder. Only local names will be reported.
     * @param projectFolder list of folder names
     * @return
     */
    public static @Nonnull Set<String> getIntellijProjectFiles(File projectFolder) {
        Set<String> set = new HashSet<>(1);
        checkAndAdd(set,projectFolder,".idea");
        return Collections.unmodifiableSet(set);
    }

    /**
     * Extract the names of intellij-specific files and folder. Only local names will be reported.
     * @param projectFolder list of folder names
     * @return
     */
    public static @Nonnull Set<String> getIDEProjectFiles(File projectFolder) {
        Set<String> set = new HashSet<>();
        set.addAll(getEclipseProjectFiles(projectFolder));
        set.addAll(getIntellijProjectFiles(projectFolder));
        return Collections.unmodifiableSet(set);
    }


    private static void checkAndAdd(Set<String> set, File projectFolder, String fileName) {
        if (new File(projectFolder,fileName).exists()) {
            set.add(fileName);
        }
    }
}
