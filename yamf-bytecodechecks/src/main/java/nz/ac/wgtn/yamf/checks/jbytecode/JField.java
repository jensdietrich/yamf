package nz.ac.wgtn.yamf.checks.jbytecode;

import java.util.Objects;

/**
 * Representation of a Java field, mainly to be used to build predicates.
 * @author jens dietrich
 */
public class JField extends JArtifact {

    private String name = null;
    private String descriptor = null;

    public JField(String name, String descriptor, int modifiers) {
        super(modifiers);
        this.name = name;
        this.descriptor = descriptor;
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JField jField = (JField) o;
        return Objects.equals(name, jField.name) &&
                Objects.equals(descriptor, jField.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, descriptor);
    }
}
