package nz.ac.wgtn.yamf.checks.jbytecode;

import com.google.common.base.Preconditions;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import java.io.File;
import java.util.stream.Stream;

/**
 * Some simple actions.
 * @author jens dietrich
 */
public class JByteCodeActions {

    private static class JClassBuilder extends ClassVisitor {
        JClass clazz = null;
        public JClassBuilder() {
            super(ASMCommons.ASM_VERSION);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            clazz = new JClass(access,name);
            clazz.setName(convertClassName(name));
            clazz.setSuperClass(convertClassName(superName));
            if (interfaces!=null) {
                Stream.of(interfaces)
                    .map(i -> convertClassName(name))
                    .forEach(i -> clazz.addInterface(i));
            }
        }

        private AnnotationVisitor collectAnnotations(JArtifact annotatable, String descriptor, boolean visible) {
            if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
                String annotation = convertClassName(descriptor);
                annotatable.addAnnotation(annotation);
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return collectAnnotations(clazz,descriptor,visible);
        }

        @Override public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            JMethod method = new JMethod(name,descriptor,exceptions,access);
            clazz.addMethod(method);
            return new MethodVisitor(ASMCommons.ASM_VERSION) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return collectAnnotations(method,descriptor,visible);
                }
            };
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            JField field = new JField(name,descriptor,access);
            clazz.addField(field);
            return new FieldVisitor(ASMCommons.ASM_VERSION) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return collectAnnotations(field,descriptor,visible);
                }
            };
        }

        private String convertClassName(String name) {
            if (name.endsWith(";")) {
                name.startsWith("L");
                name = name.substring(1,name.length()-1);
            }
            return name.replace("/",".");
        }
    }


    public static JClass getClass(File file) throws Exception {
        Preconditions.checkArgument(file.exists(),"File " + file.getAbsolutePath() + " does not exist");
        JClassBuilder builder = new JClassBuilder();
        ASMCommons.analyse(file,builder);
        return builder.clazz;
    }


}
