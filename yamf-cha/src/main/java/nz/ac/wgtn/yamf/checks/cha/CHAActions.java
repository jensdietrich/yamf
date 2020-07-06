package nz.ac.wgtn.yamf.checks.cha;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.checks.jbytecode.*;
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
     * Build the call graph for a set of classes. The callgraph is not very accurate, the main purpose is to assess whether a classs or method uses
     * some external target methods, usually provided by the system or third party libaries.
     * The callgraph is build for the project classes passed, and ends with calls into libraries. I.e., library / system internal calls are not modelled.
     * @param projectClassFiles
     * @param classpath
     * @return
     * @throws Exception
     */

    public static CallGraph constructCallGraph(Set<File> projectClassFiles,List<File> classpath) throws Exception {

        validateClassFiles(projectClassFiles);
        validateClasspath(classpath);

        Map<String,JClass> projectClasses = buildProjectClasses(projectClassFiles);
        Map<String,File> classesLocations = indexLibrariesInClasspath(classpath);
        TypeHierarchy typeHierarchy = constructTypeHierachy(projectClasses, classesLocations);

        CallGraph callgraph = new CallGraph();

        // add vertices for project classes
        projectClasses.values().stream()
            .flatMap(cl -> cl.getMethods().stream())
            .forEach(m -> callgraph.addVertex(m));

        // add edges
        for (JClass clazz:projectClasses.values()) {
            for (JMethod method:clazz.getMethods()) {
                for (Invocation invocation:method.getInvocations()) {
                    String ownerName = invocation.getOwner();
                    JClass owner = projectClasses.get(ownerName);
                    if (owner==null) {
                        owner = typeHierarchy.getClass(ownerName);
                        if (owner==null) {
                            owner = addClassToHierarchy(ownerName, projectClasses, typeHierarchy, classesLocations);
                        }
                    }
                    // find target
                    try {
                        for (JMethod target : owner.getMethods()) {
                            if (Objects.equals(target.getName(), invocation.getName()) && Objects.equals(target.getDescriptor().getRawDescriptor(), invocation.getDescriptor().getRawDescriptor())) {
                                callgraph.addEdge(new InvocationEdge(), method, target);
                            }
                        }
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            }
        }
        return callgraph;

    }

//    private static String idx (Invocation invocation) {
//        return invocation.getName() + invocation.getDescriptor();
//    }
//
//    private static String idx (JMethod method) {
//        String hash = method.getName();
//        hash = hash + '(';
//        for (String paramType:method.getParameterTypes()) {
//            hash = hash + convertTypeNameToInternal(paramType)
//        }
//        hash = hash + ')';
//        return hash + convertTypeNameToInternal(method.getReturnType());
//    }
//
//    private static String convertTypeNameToInternal(String type) {
//        if (Objects.equals(type,"void")) {
//            return "V";
//        }
//        if (Objects.equals(type,"int")) {
//            return "I";
//        }
//        if (Objects.equals(type,"boolean")) {
//            return "Z";
//        }
//        if (Objects.equals(type,"char")) {
//            return "C";
//        }
//        if (Objects.equals(type,"byte")) {
//            return "B";
//        }
//        if (Objects.equals(type,"short")) {
//            return "S";
//        }
//        if (Objects.equals(type,"float")) {
//            return "F";
//        }
//        if (Objects.equals(type,"long")) {
//            return "J";
//        }
//        if (Objects.equals(type,"double")) {
//            return "D";
//        }
//        if (type.endsWith("[]")) {
//            return "[" + convertTypeNameToInternal(type.substring(0,type.length()-2));
//        }
//        else {
//            return "L" + type.replace(".","/") + ';';
//        }
//    }

    /**
     * Build the class hierarchy for a set of classes.
     * This computes the transitive closure of the extends and implements relationship w.r.t. classes.
     * If a supertype cannot be resolved, try to use the standard library with reflection (using the JRE used to execute this).
     * If a supertype can still not be resolved, log and ignore.
     * @param projectClassFiles
     * @param classpath
     * @return
     * @throws Exception
     */
    public static TypeHierarchy constructTypeHierachy(Set<File> projectClassFiles, List<File> classpath) throws Exception {
        validateClassFiles(projectClassFiles);
        validateClasspath(classpath);
        Map<String,JClass> projectClasses = buildProjectClasses(projectClassFiles);
        Map<String,File> classesLocations = indexLibrariesInClasspath(classpath);
        return constructTypeHierachy(projectClasses, classesLocations);
    }

    private static TypeHierarchy constructTypeHierachy(Map<String,JClass> projectClasses, Map<String,File> classesLocations)  {

        TypeHierarchy hierarchy = new TypeHierarchy();
        projectClasses.keySet().stream()
            .forEach(name -> {
                try {
                    addClassToHierarchy(name,projectClasses,hierarchy,classesLocations);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        return hierarchy;
    }

    /**
     * Produce a map associating class names with the libraries containing the respective class definition.
     * If there are multiple definitions, the first one wins (modelling the semantics of the classpath)
     * @param classpath
     * @return
     * @throws IOException
     */
    private static Map<String,File> indexLibrariesInClasspath(List<File> classpath) throws IOException {
        Map<String,File> classesLocations = new HashMap<>();
        for (File classpathElement:classpath) {
            ZipFile jar = new ZipFile(classpathElement);
            Enumeration<? extends ZipEntry> en = jar.entries();
            while (en.hasMoreElements()) {
                ZipEntry e = en.nextElement();
                String name = e.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0,name.length()-6);
                    name = name.replaceAll("/",".");
                    classesLocations.computeIfAbsent(name, n -> classpathElement);
                }
            }
        }
        return classesLocations;
    }

    private static Map<String,JClass> buildProjectClasses(Set<File> projectClassFiles) throws Exception {
        Map<String,JClass> projectClasses = new HashMap<>();
        projectClassFiles.stream()
            .map(classFile -> {
                try {
                    return JByteCodeActions.getClass(classFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(cl -> cl!=null)
            .forEach(cl -> projectClasses.put(cl.getName(),cl));
        return projectClasses;
    }

//    private static JClass findOrConstructClass(String name, Map<String, JClass> classesByName, Map<String, File> classesLocation) throws Exception {
//        // see whether class is already known
//        JClass clazz = classesByName.get(name);
//        if (clazz!=null) {
//            return clazz;
//        }
//
//        // try to find class in (jar in) classpath, and build from bytecode
//        File location = classesLocation.get(name);
//        if (location!=null) {
//            clazz = JByteCodeActions.getClass(location,name);
//            assert clazz!=null; // otherwise indexing has a bug
//            return clazz;
//        }
//
//        // try to find class using reflection
//        try {
//            return ReflectionActions.getClass(name);
//        }
//        catch (Exception x) {
//            System.out.println("Could not find class " + name);
//            return null;
//        }
//    }

    private static JClass addClassToHierarchy(String name, Map<String,JClass> coreProjectClasses, TypeHierarchy typeHierarchy, Map<String, File> classesLocations) throws Exception {
        // see whether class is already known
        JClass clazz = typeHierarchy.getVertexByName(name);
        if (clazz!=null) {
            return clazz; // already added
        }

        clazz = coreProjectClasses.get(name);

        // try to find class in (jar in) classpath, and build from bytecode
        if (clazz==null) {
            File location = classesLocations.get(name);
            if (location != null) {
                clazz = JByteCodeActions.getClass(location, name);
                assert clazz != null; // otherwise indexing has a bug
            } else {
                // try to find class using reflection
                try {
                    clazz = ReflectionActions.getClass(name);
                } catch (Exception x) {
                    System.out.println("Could not find class using reflection " + name);
                    return null;
                }
            }
        }
        assert clazz!=null;
        typeHierarchy.addVertex(clazz);

        // add supertypes and edges
        if (clazz.getSuperClass()!=null) {
            JClass superClass = addClassToHierarchy(clazz.getSuperClass(),coreProjectClasses,typeHierarchy,classesLocations);
            if (superClass!=null) {
                typeHierarchy.addEdge(new ExtendsEdge(),clazz,superClass);
            }
        }
        for (String interfaceName:clazz.getInterfaces()) {
            JClass intrfc = addClassToHierarchy(interfaceName,coreProjectClasses,typeHierarchy,classesLocations);
            if (intrfc!=null) {
                typeHierarchy.addEdge(new ExtendsEdge(),clazz,intrfc);
            }
        }

        return clazz;

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

    private static Set<JMethod> collectNonAbstractMethods (TypeHierarchy typeHierarchy, JClass clazz) {
        Preconditions.checkArgument(typeHierarchy.containsVertex(clazz));
        Set<JMethod> methods = new HashSet<>();
        methods.addAll(clazz.getMethods().stream().filter(m -> !m.isAbstract()).collect(Collectors.toSet()));
        Set<JMethod> methodsFromSuper = typeHierarchy.getOutEdges(clazz).stream()   // include interfaces for default methods
            .map(e -> typeHierarchy.getDest(e))
            .flatMap(cl -> collectNonAbstractMethods (typeHierarchy, cl).stream())
            .collect(Collectors.toSet());
        methods.addAll(methodsFromSuper);
        return methods;
    }

}
