package nz.ac.wgtn.yamf.checks.jbytecode;

import org.objectweb.asm.Opcodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract supertype for classes, methods etc.
 * @author jens dietrich
 */
public class JArtifact {
    protected int modifiers = 0;
    protected List<JAnnotation> annotations = new ArrayList<>();

    public JArtifact(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }

    public List<JAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public void addAnnotation(JAnnotation annotation) {
        this.annotations.add(annotation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JArtifact jArtifact = (JArtifact) o;
        return modifiers == jArtifact.modifiers && Objects.equals(annotations, jArtifact.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiers, annotations);
    }

    public boolean isPublic() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_PUBLIC);
    }

    public boolean isPrivate() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_PRIVATE);
    }

    public boolean isProtected() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_PROTECTED);
    }

    public boolean isFinal() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_FINAL);
    }

    public boolean isSynthetic() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_SYNTHETIC);
    }

    public boolean isStatic() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_STATIC);
    }

    public boolean isAbstract() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_ABSTRACT);
    }

    public boolean isInterface() {
        return ASMCommons.checkFlag(modifiers, Opcodes.ACC_INTERFACE);
    }


}
