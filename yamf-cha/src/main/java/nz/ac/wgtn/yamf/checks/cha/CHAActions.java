package nz.ac.wgtn.yamf.checks.cha;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * CHA related actions.
 * @author jens dietrich
 */
public class CHAActions {

    /**
     * Build the class hierarchy for a set of classes.
     * This computes the transitive closure of the extends and implements relationship w.r.t. classes.
     * If a supertype cannot be resolved, try to use the standard library with reflection (using the JRE used to execute this).
     * If a supertype can still not be resolved, log and ignore.
     * @param classFiles
     * @param classpath
     * @return
     * @throws Exception
     */
    public static TypeHierarchy getTypeHierachy(Set<File> classFiles,Collection<File> classpath) throws Exception {

        validateClassFiles(classFiles);
        validateClasspath(classpath);

        // build classes
        Set<JClass> classes = classFiles.stream()
            .map(classFile -> {
                try {
                    return JByteCodeActions.getClass(classFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(classFile -> classFile!=null)
            .collect(Collectors.toSet());

        // index classes in jar files
        Map<String,File> classesLocation = new HashMap<>();
        for (File classpathElement:classpath) {
            ZipFile jar = new ZipFile(classpathElement);
            Enumeration<? extends ZipEntry> en = jar.entries();
            while (en.hasMoreElements()) {
                ZipEntry e = en.nextElement();
                String name = e.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0,name.length()-6);
                    name = name.replaceAll("/",".");
                    classesLocation.computeIfAbsent(name, n -> classpathElement);
                }
            }
        }

        TypeHierarchy hierarchy = new TypeHierarchy();
        Queue<JClass> queue = new LinkedList();
        Map<String,JClass> classesByName = new HashMap<>();
        Set<JClass> done = new HashSet<>();
        queue.addAll(classes);
        for (JClass clazz:classes) {
            classesByName.put(clazz.getName(),clazz);
            hierarchy.addVertex(clazz);
        }
        while (!queue.isEmpty()) {
            JClass next = queue.poll();
            assert next!=null;
            if (done.add(next)) {
                String superClassName = next.getSuperClass();
                if (superClassName!=null) {
                    JClass superClass = findOrConstructClass(superClassName, classesByName, classesLocation);
                    if (superClass != null) {
                        classesByName.put(superClassName, superClass);
                        hierarchy.addEdge(new ExtendsEdge(), next, superClass);
                        queue.add(superClass);
                    }
                }
                for (String interfaceName:next.getInterfaces()) {
                    JClass itrfc = findOrConstructClass(interfaceName, classesByName, classesLocation);
                    classesByName.put(interfaceName, itrfc);
                    hierarchy.addEdge(new ImplementsEdge(), next, itrfc);
                    queue.add(itrfc);
                }
            }
        }

        return hierarchy;

    }

    private static JClass findOrConstructClass(String name, Map<String, JClass> classesByName, Map<String, File> classesLocation) throws Exception {
        // see whether class is already known
        JClass clazz = classesByName.get(name);
        if (clazz!=null) {
            return clazz;
        }

        // try to find class in (jar in) classpath, and build from bytecode
        File location = classesLocation.get(name);
        if (location!=null) {
            clazz = JByteCodeActions.getClass(location,name);
            assert clazz!=null; // otherwise indexing has a bug
            return clazz;
        }

        // try to find class using reflection
        try {
            return ReflectionActions.getClass(name);
        }
        catch (Exception x) {
            System.out.println("Could not find class " + name);
            return null;
        }
    }


    private static void validateClassFiles(Collection<File> classFiles)  {
        for (File classFile:classFiles) {
            Preconditions.checkArgument(classFile.exists(),"Class file does not exist: " + classFile.getAbsolutePath());
            Preconditions.checkArgument(!classFile.isDirectory(),"Class files must be files with existion .class: " + classFile.getAbsolutePath());
            Preconditions.checkArgument(classFile.getName().endsWith(".class"),"Class files must be files with existion .class: " + classFile.getAbsolutePath());
        }
    }

    private static void validateClassFileFolder(File classFileFolder) throws IOException {
        boolean hasClassFile = Files.walk(Paths.get(classFileFolder.getAbsolutePath()))
            .anyMatch(path -> path.toFile().getName().endsWith(".class"));
        Preconditions.checkArgument(hasClassFile,"Folder does not contain .class files: " + classFileFolder.getAbsolutePath());
    }

    private static void validateClasspath(Collection<File> classpath)  {
        for (File classpathElement:classpath) {
            Preconditions.checkArgument(classpathElement.exists(),"Class path element does not exist: " + classpathElement.getAbsolutePath());
            Preconditions.checkArgument(!classpathElement.isDirectory(),"Class path elements must be jar files: " + classpathElement.getAbsolutePath());
            Preconditions.checkArgument(classpathElement.getName().endsWith(".jar"),"Class path elements must be jar files: " + classpathElement.getAbsolutePath());
        }
    }


}
