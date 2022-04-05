package nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.ConditionNotSatisfiedHandler;
import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.jbytecode.descr.MethodDescriptor;
import org.objectweb.asm.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

/**
 * Some simple actions.
 * @author jens dietrich
 */
public class JByteCodeActions {

    private static AnnotationVisitor collectAnnotations(JArtifact annotatable, String descriptor) {
        assert descriptor.startsWith("L") && descriptor.endsWith(";");
        String name = convertClassName(descriptor);
        JAnnotation annotation = new JAnnotation(0, name);
        annotatable.addAnnotation(annotation);
        return new JAnnotationVisitor(annotation);
    }

    private static class JAnnotationVisitor extends AnnotationVisitor {

        public JAnnotationVisitor(JAnnotation annotation) {
            super(ASMCommons.ASM_VERSION);
            this.annotation = annotation;
        }

        private JAnnotation annotation = null;

        @Override
        public void visit(String name, Object value) {
            annotation.setProperty(name,value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            String annotationClassName = convertClassName(descriptor);
            String annotationValue = annotationClassName + '.' + value;
            annotation.setProperty(name,annotationValue);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return collectAnnotations(annotation,descriptor);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            List<Object> values = new ArrayList<>();
            annotation.setProperty(name,values);
            return new AnnotationVisitor(ASMCommons.ASM_VERSION) {
                @Override
                public void visit(String name, Object value) {
                    super.visit(name, value);
                    values.add(value);
                }

                @Override
                public void visitEnum(String name, String descriptor, String value) {
                    String annotationClassName = convertClassName(descriptor);
                    String annotationValue = annotationClassName + '.' + value;
                    values.add(annotationValue);
                }

                @Override
                public AnnotationVisitor visitArray(String name) {
                    System.err.println("unsupported analysis for nested arrays in annotation properties");
                    return null;
                }
            };
        }

    }

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

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return collectAnnotations(clazz,descriptor);
        }

        @Override public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            JMethod method = new JMethod(clazz,name,descriptor,exceptions,access);
            clazz.addMethod(method);
            return new MethodVisitor(ASMCommons.ASM_VERSION) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return collectAnnotations(method,descriptor);
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
            JField field = new JField(clazz,name,DescriptorParser.parseFieldDescriptor(descriptor),access);
            clazz.addField(field);
            return new FieldVisitor(ASMCommons.ASM_VERSION) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return collectAnnotations(field,descriptor);
                }
            };
        }

    }

    public static JClass getClass(File file, ConditionNotSatisfiedHandler feh) throws Exception {
        if (feh.handle(file==null,"File is null")) {
            return null;
        }
        if (!feh.handle(file.exists(),"File " + file.getAbsolutePath() + " does not exist")) {
            return null;
        }
        JClassBuilder builder = new JClassBuilder();
        ASMCommons.analyse(file,builder);
        return builder.clazz;
    }

    public static JClass getClass(File jar,String name, ConditionNotSatisfiedHandler feh) throws Exception {
        if (feh.handle(jar==null,"Jar file is null")) {
            return null;
        }
        if (feh.handle(name==null,"name is null")) {
            return null;
        }
        if (!feh.handle(jar.exists(),"File " + jar.getAbsolutePath() + " does not exist")) {
            return null;
        }
        JClassBuilder builder = new JClassBuilder();
        ASMCommons.analyse(new ZipFile(jar),name,builder);
        return builder.clazz;
    }

    private static String convertClassName(String name) {
        if (name.endsWith(";")) {
            name.startsWith("L");
            name = name.substring(1,name.length()-1);
        }
        return name.replace("/",".");
    }

}
