package nz.ac.wgtn.yamf.checks.cha;

import org.objectweb.asm.Opcodes;
import java.lang.reflect.Modifier;

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


}
