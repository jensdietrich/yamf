package nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorUtil;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Actions to build JClasses using the standard library of the JRE running.
 * @author jens dietrich
 */
public class ReflectionActions {


    public static JClass getClass(String name) throws Exception {
        Class clazz = Class.forName(name);

        JClass jclass = new JClass(adaptReflectionFlags(clazz.getModifiers()),name);
        jclass.setSuperClass(clazz.getSuperclass()==null?null:clazz.getSuperclass().getName());

        for (Class intrfc:clazz.getInterfaces()) {
            jclass.addInterface(intrfc.getName());
        }

        for (Method method:clazz.getDeclaredMethods()) {

            System.out.println("Adding " + method + " to " + clazz);
            String descriptor = DescriptorUtil.constructBytecodeDescriptor(method.getParameterTypes(),method.getReturnType());
            String[] exceptionTypes = Stream.of(method.getExceptionTypes()).map(cl -> cl.getName()).toArray(size -> new String[method.getExceptionTypes().length]);
            JMethod jmethod = new JMethod(jclass,method.getName(),descriptor,exceptionTypes,adaptReflectionFlags(method.getModifiers()));
            jclass.addMethod(jmethod);
        }

        for (Field field:clazz.getDeclaredFields()) {
            String descriptor = convertTypeNameToInternal(field.getType().getName());
            JField jfield = new JField(jclass,field.getName(),descriptor,adaptReflectionFlags(field.getModifiers()));
            jclass.addField(jfield);
        }

        // TODO add methods, fields and annotations (main use case is hierarchy analysis, but this may change)
        return jclass;
    }

    private static int adaptReflectionFlags (int reflFlags) {
        int flags = 0;
        if (Modifier.isAbstract(reflFlags)) {
            flags = flags & Opcodes.ACC_ABSTRACT;
        }
        if (Modifier.isPublic(reflFlags)) {
            flags = flags & Opcodes.ACC_PUBLIC;
        }
        if (Modifier.isPrivate(reflFlags)) {
            flags = flags & Opcodes.ACC_PRIVATE;
        }
        if (Modifier.isProtected(reflFlags)) {
            flags = flags & Opcodes.ACC_PROTECTED;
        }
        if (Modifier.isFinal(reflFlags)) {
            flags = flags & Opcodes.ACC_FINAL;
        }
        if (Modifier.isStatic(reflFlags)) {
            flags = flags & Opcodes.ACC_STATIC;
        }
        if (Modifier.isSynchronized(reflFlags)) {
            flags = flags & Opcodes.ACC_SYNCHRONIZED;
        }
        if (Modifier.isNative(reflFlags)) {
            flags = flags & Opcodes.ACC_NATIVE;
        }
        if (Modifier.isVolatile(reflFlags)) {
            flags = flags & Opcodes.ACC_VOLATILE;
        }
        if (Modifier.isInterface(reflFlags)) {
            flags = flags & Opcodes.ACC_INTERFACE;
        }
        if (Modifier.isStrict(reflFlags)) {
            flags = flags & Opcodes.ACC_STRICT;
        }
        if (Modifier.isTransient(reflFlags)) {
            flags = flags & Opcodes.ACC_TRANSIENT;
        }
        return flags;
    }

    private static String convertTypeNameToInternal(String type) {
        if (Objects.equals(type,"void")) {
            return "V";
        }
        if (Objects.equals(type,"int")) {
            return "I";
        }
        if (Objects.equals(type,"boolean")) {
            return "Z";
        }
        if (Objects.equals(type,"char")) {
            return "C";
        }
        if (Objects.equals(type,"byte")) {
            return "B";
        }
        if (Objects.equals(type,"short")) {
            return "S";
        }
        if (Objects.equals(type,"float")) {
            return "F";
        }
        if (Objects.equals(type,"long")) {
            return "J";
        }
        if (Objects.equals(type,"double")) {
            return "D";
        }
        if (type.endsWith("[]")) {
            return "[" + convertTypeNameToInternal(type.substring(0,type.length()-2));
        }
        else {
            return "L" + type.replace(".","/") + ';';
        }
    }



}
