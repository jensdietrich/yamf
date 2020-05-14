package nz.ac.vuw.yamf.checks.jbytecode;

import org.objectweb.asm.Opcodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static nz.ac.vuw.yamf.checks.jbytecode.ASMCommons.checkFlag;

/**
 * Abstract supertype for classes, methods etc.
 * @author jens dietrich
 */
public class JArtifact {
    protected int modifiers = 0;
    protected List<String> annotations = new ArrayList<>();

    public JArtifact(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }

    public List<String> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public void addAnnotation(String annotation) {
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
        return checkFlag(modifiers, Opcodes.ACC_PUBLIC);
    }

    public boolean isPrivate() {
        return checkFlag(modifiers, Opcodes.ACC_PRIVATE);
    }

    public boolean isProtected() {
        return checkFlag(modifiers, Opcodes.ACC_PROTECTED);
    }

    public boolean isFinal() {
        return checkFlag(modifiers, Opcodes.ACC_FINAL);
    }

    public boolean isSynthetic() {
        return checkFlag(modifiers, Opcodes.ACC_SYNTHETIC);
    }

    public boolean isStatic() {
        return checkFlag(modifiers, Opcodes.ACC_STATIC);
    }


}
