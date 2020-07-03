package nz.ac.wgtn.yamf.checks.cha;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.checks.cha.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.cha.descr.MethodDescriptor;
import org.objectweb.asm.*;
import java.io.File;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

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
                    .map(i -> convertClassName(i))
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

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

                    Invocation.Kind kind = null;
                    if (ASMCommons.checkFlag(opcode, Opcodes.INVOKEINTERFACE) ) {
                        kind = Invocation.Kind.INVOKEINTERFACE;
                    }
                    else if (ASMCommons.checkFlag(opcode, Opcodes.INVOKESPECIAL) ) {
                        kind = Invocation.Kind.INVOKESPECIAL;
                    }
                    else if (ASMCommons.checkFlag(opcode, Opcodes.INVOKEVIRTUAL) ) {
                        kind = Invocation.Kind.INVOKEVIRTUAL;
                    }
                    else if (ASMCommons.checkFlag(opcode, Opcodes.INVOKESTATIC) ) {
                        kind = Invocation.Kind.INVOKESTATIC;
                    }
                    assert kind != null;

                    String clazz = DescriptorParser.parseType(owner);
                    MethodDescriptor descr = DescriptorParser.parseMethodDescriptor(descriptor);

                    Invocation invocation = new Invocation(clazz,name,descr,kind);
                    method.addInvocation(invocation);
                }

            };
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            JField field = new JField(name,DescriptorParser.parseFieldDescriptor(descriptor),access);
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

    public static JClass getClass(File jar,String name) throws Exception {
        Preconditions.checkArgument(jar.exists(),"File " + jar.getAbsolutePath() + " does not exist");
        JClassBuilder builder = new JClassBuilder();
        ASMCommons.analyse(new ZipFile(jar),name,builder);
        return builder.clazz;
    }

}
