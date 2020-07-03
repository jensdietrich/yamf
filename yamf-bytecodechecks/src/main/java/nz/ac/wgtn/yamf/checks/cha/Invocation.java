package nz.ac.wgtn.yamf.checks.cha;

import nz.ac.wgtn.yamf.checks.cha.descr.MethodDescriptor;
import java.util.Objects;

/**
 * Represents method invocations. Note: does not support invokedynamic.
 * @author jens dietrich
 */
public class Invocation {

    enum Kind {INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE}

    private String owner = null;
    private String name = null;
    private MethodDescriptor descriptor = null;
    private Kind kind = null;

    public Invocation(String owner, String name, MethodDescriptor descriptor,Kind kind) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.kind = kind;
    }

    public String getOwner() {
        return owner;
    }

    public Kind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public MethodDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invocation that = (Invocation) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(name, that.name) &&
                Objects.equals(descriptor, that.descriptor) &&
                kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name, descriptor, kind);
    }
}
