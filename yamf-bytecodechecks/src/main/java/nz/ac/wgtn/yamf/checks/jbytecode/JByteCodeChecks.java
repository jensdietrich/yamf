package nz.ac.wgtn.yamf.checks.jbytecode;

import org.junit.jupiter.api.Assertions;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Some simple bytecode checks. Use actions first to build a JCLass from a .class file.
 * @author jens dietrich
 */
public class JByteCodeChecks {


    public static void assertDirectlyExtendsSuperClass(JClass clazz, String superClass) throws Exception {
        Assertions.assertEquals(superClass,clazz.getSuperClass(),"This class does not directly extend " + superClass);
    }

    public static void assertDirectlyImplementsInterface(JClass clazz, String itrfc) throws Exception {
        Assertions.assertTrue(clazz.getInterfaces().contains(itrfc),"This class does not directly implement " + itrfc);
    }

    public static void assertDirectlyExtendsOrImplementsAny(JClass clazz, String... types) throws Exception {
        boolean match = Stream.of(types).anyMatch(t -> Objects.equals(t,clazz.getSuperClass()));
        if (!match) {
            match = Stream.of(types).anyMatch(t -> clazz.getInterfaces().contains(t));
        }
        Assertions.assertTrue(match,"This class does not directly extend or implement any of the following " + Stream.of(types).collect(Collectors.joining(",")));
    }

    public static void assertHasAnyFieldSuchThat(JClass clazz, Predicate<JField> test,String condition) throws Exception {
        boolean match = clazz.getFields().stream().anyMatch(test);
        Assertions.assertTrue(match,"There is no field satisfying the condition \"" + condition + "\" in " + clazz.getName());
    }

    public static void assertHasAnyMethodSuchThat(JClass clazz, Predicate<JMethod> test,String condition) throws Exception {
        boolean match = clazz.getMethods().stream().anyMatch(test);
        Assertions.assertTrue(match,"There is no method satisfying the condition \"" + condition + "\" in " + clazz.getName());;
    }

    public static void assertHasJUnit5Tests(JClass clazz) throws Exception {
        // test methods do not have to be public, but they cannot be private:
        // https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods
        assertHasAnyMethodSuchThat(clazz, method -> !method.isPrivate() && !method.isStatic() && method.isJunit5Test(),
      "class should contain junit5 test method (public, non-static, annotated with @org.junit.jupiter.api.Test)"
        );
    }


    public static void assertHasJUnit4Tests(JClass clazz) throws Exception {
        // junit4 methods must be public:
        // https://junit.org/junit4/javadoc/latest/org/junit/Test.html
        assertHasAnyMethodSuchThat(clazz, method -> method.isPublic() && !method.isStatic() && method.isJunit4Test(),
  "class should contain junit5 test method (public, non-static, annotated with @org.junit.Test)"
        );
    }

    public static void assertHasJUnit4or5Tests(JClass clazz) throws Exception {
        assertHasAnyMethodSuchThat(clazz,
            method -> method.isPublic() && !method.isStatic() && (method.isJunit4Test() || method.isJunit5Test()),
                "class should contain junit5 test method (public, non-static, annotated with @org.junit.Test or @org.junit.jupiter.api.Test)"
        );
    }

    public static void assertHasGetter(JClass clazz,String property,String type) {
        Assertions.assertTrue(clazz.hasGetter(property,type),"No getter found for property " + property + " with type " + type + " in class " + clazz.getName());
    }

    public static void assertHasSetter(JClass clazz,String property,String type) {
        Assertions.assertTrue(clazz.hasSetter(property,type),"No setter found for property " + property + " with type " + type + " in class " + clazz.getName());
    }

    public static void assertHasGetterAndSetter(JClass clazz,String property,String type) {
        Assertions.assertTrue(clazz.hasGetterAndSetter(property,type),"No setter + getter combination found for property " + property + " with type " + type + " in class " + clazz.getName());
    }

    public static void assertHasDefaultConstructor(JClass clazz) {
        Assertions.assertTrue(clazz.hasDefaultConstructor(),"No default constructor (public, no parameters) in class " + clazz.getName());
    }

}
