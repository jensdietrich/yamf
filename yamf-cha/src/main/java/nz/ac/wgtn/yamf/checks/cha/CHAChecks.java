package nz.ac.wgtn.yamf.checks.cha;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * CHA-related checks.
 * @author jens dietrich
 */
public class CHAChecks {

    public static void assertExtends(TypeHierarchy typeHierachy,String className, String superClassName) {
        JClass clazz = typeHierachy.getClass(className);
        Assumptions.assumeTrue(clazz!=null, "Class " + className + " not found in class hierarchy");
        Assumptions.assumeTrue(!clazz.isInterface(), "Class " + className + " is an interface, not a class");
        JClass superClazz = typeHierachy.getClass(superClassName);
        Assumptions.assumeTrue(superClazz!=null, "Class " + superClassName + " not found in class hierarchy");
        Assumptions.assumeTrue(!superClazz.isInterface(), "Class " + superClassName + " is an interface, not a class");

        boolean check = hasSuperType(typeHierachy,clazz,e -> e instanceof ExtendsEdge,cl -> Objects.equals(superClazz,cl));
        Assertions.assertTrue(check,"Class " + className + " does not extend " + superClassName);
    }

    public static void assertDoesNotExtend(TypeHierarchy typeHierachy,String className, String superClassName) {
        JClass clazz = typeHierachy.getClass(className);
        Assumptions.assumeTrue(clazz!=null, "Class " + className + " not found in class hierarchy");
        Assumptions.assumeTrue(!clazz.isInterface(), "Class " + className + " is an interface, not a class");
        JClass superClazz = typeHierachy.getClass(superClassName);
        Assumptions.assumeTrue(superClazz!=null, "Class " + superClassName + " not found in class hierarchy");
        Assumptions.assumeTrue(!superClazz.isInterface(), "Class " + superClassName + " is an interface, not a class");

        boolean check = hasSuperType(typeHierachy,clazz,e -> e instanceof ExtendsEdge,cl -> Objects.equals(superClazz,cl));
        Assertions.assertFalse(check,"Class " + className + " does extend " + superClassName);
    }

    public static void assertImplements(TypeHierarchy typeHierachy,String className, String interfaceName) {
        JClass clazz = typeHierachy.getClass(className);
        Assumptions.assumeTrue(clazz!=null, "Class " + className + " not found in class hierarchy");
        Assumptions.assumeTrue(!clazz.isInterface(), "Class " + className + " is an interface, not a class");
        JClass theInterface = typeHierachy.getClass(interfaceName);
        Assumptions.assumeTrue(theInterface!=null, "Class " + interfaceName + " not found in class hierarchy");
        Assumptions.assumeTrue(theInterface.isInterface(), "Class " + interfaceName + " is a class, not an interface");

        boolean check = hasSuperType(typeHierachy,clazz,e -> true,cl -> Objects.equals(theInterface,cl));
        Assertions.assertTrue(check,"Class " + className + " does not implement " + interfaceName);
    }

    public static void assertDoesNotImplement(TypeHierarchy typeHierachy,String className, String interfaceName) {
        JClass clazz = typeHierachy.getClass(className);
        Assumptions.assumeTrue(clazz!=null, "Class " + className + " not found in class hierarchy");
        Assumptions.assumeTrue(!clazz.isInterface(), "Class " + className + " is an interface, not a class");
        JClass theInterface = typeHierachy.getClass(interfaceName);
        Assumptions.assumeTrue(theInterface!=null, "Class " + interfaceName + " not found in class hierarchy");
        Assumptions.assumeTrue(theInterface.isInterface(), "Class " + interfaceName + " is a class, not an interface");

        boolean check = hasSuperType(typeHierachy,clazz,e -> true,cl -> Objects.equals(theInterface,cl));
        Assertions.assertFalse(check,"Class " + className + " does implement " + interfaceName);
    }

    private static boolean hasSuperType(TypeHierarchy typeHierarchy,JClass type, Predicate<Edge> edgeFilter, Predicate<JClass> acceptanceTest) {
        Queue<JClass> queue = new LinkedList<>();
        Set<JClass> superTypes = typeHierarchy.getOutEdges(type).stream()
            .filter(e -> edgeFilter.test(e))
            .map(e -> typeHierarchy.getDest(e))
            .collect(Collectors.toSet());
        queue.addAll(superTypes);

        while (!queue.isEmpty()) {
            JClass next = queue.poll();
            if (acceptanceTest.test(next)) {
                return true;
            }
            superTypes = typeHierarchy.getOutEdges(next).stream()
                    .filter(e -> edgeFilter.test(e))
                    .map(e -> typeHierarchy.getDest(e))
                    .collect(Collectors.toSet());
            queue.addAll(superTypes);
        }

        return false;
    }
}
