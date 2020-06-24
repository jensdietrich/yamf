package nz.ac.wgtn.yamf.commons;

import com.google.common.base.Preconditions;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Predicate;

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

    /**
     * Find an ancestor satidfying a certain condition.
     * @param file
     * @param condition
     * @param includeSelf -- whether file is also being considered
     * @return
     */
    public static File findAncestorSuchThat(File file, Predicate<File> condition, boolean includeSelf) {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists());
        if (includeSelf && condition.test(file)) {
            return file;
        }
        return doFindAncestorSuchThat(file.getParentFile(),condition);
    }

    private static File doFindAncestorSuchThat(File file, Predicate<File> condition) {
        if (condition.test(file)) {
            return file;
        }
        File parent = file.getParentFile();
        if (parent==null) {
            return null;
        }
        else {
            return doFindAncestorSuchThat(parent,condition);
        }
    }

    /**
     * Breath-first traverse children to find a child file or folder satisfying a condition.
     * Use case: students submit maven projects, but sometimes this is in a subfolder (not in root), so this can be used
     * to find the folder with pom.xml closest to the root.
     * Note that this uses BF traversal while nio walkFileTree uses DF traversal.
     * @param root
     * @return
     */
    public static File findTopMostChildSuchThat(File root, Predicate<File> condition) {
        Preconditions.checkNotNull(root);
        Preconditions.checkArgument(root.exists());
        Preconditions.checkArgument(root.isDirectory());
        Queue<File> queue = new ArrayDeque<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            File f = queue.remove();
            if (condition.test(f)) {
                return f;
            }
            // add next generation
            if (f.isDirectory()) {
                for (File child : f.listFiles()) {
                    queue.add(child);
                }
            }
        }

        return null;
    }

}
