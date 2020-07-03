package nz.ac.wgtn.yamf.checks.cha;

import nz.ac.wgtn.yamf.checks.cha.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.cha.descr.MethodDescriptor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract representation of a Java method, mainly to be used to build predicates.
 * @author jens dietrich
 */
public class JMethod extends JArtifact {

    private String name = null;
    private MethodDescriptor descriptor = null;
    private List<String> exceptions = null;
    private Set<Invocation> invocations = new HashSet<>();

    public JMethod(String name, String descr, String[] excepts, int modifiers) {
        super(modifiers);
        this.name = name;
        this.descriptor = DescriptorParser.parseMethodDescriptor(descr);
        this.exceptions = excepts==null
            ? Collections.EMPTY_LIST
            : Stream.of(excepts).map(x -> x.replace("/",".")).collect(Collectors.toList());
    }

    void addInvocation(Invocation invocation) {
        this.invocations.add(invocation);
    }

    public Set<Invocation> getInvocations() {
        return invocations;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return this.descriptor.getReturnType();
    }

    public List<String> getParameterTypes() {
        return Collections.unmodifiableList(this.descriptor.getParamTypes());
    }

    public List<String> getExceptions() {
        return Collections.unmodifiableList(this.exceptions);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JMethod jMethod = (JMethod) o;
        return Objects.equals(name, jMethod.name) &&
                Objects.equals(descriptor, jMethod.descriptor) &&
                Objects.equals(exceptions, jMethod.exceptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, descriptor, exceptions);
    }


    // several useful helper methods
    public boolean isJunit4Test() {
        return !this.isPrivate() && !this.isStatic() && this.getAnnotations().contains("org.junit.Test");
    }

    public boolean isJunit5Test() {
        return !this.isPrivate() && !this.isStatic() && this.getAnnotations().contains("org.junit.jupiter.api.Test");
    }

    public boolean isConstructor() {
        return this.name.equals("<init>");
    }

    public boolean isStaticBlock() {
        return this.name.equals("<clinit>");
    }
}
