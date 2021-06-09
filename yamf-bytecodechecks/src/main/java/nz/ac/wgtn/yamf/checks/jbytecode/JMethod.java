package nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.jbytecode.descr.MethodDescriptor;

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
    private JClass owner = null;
    private List<String> exceptions = null;
    private Set<Invocation> invocations = new HashSet<>();

    public JMethod(JClass owner,String name, String descr, String[] excepts, int modifiers) {
        super(modifiers);
        this.name = name;
        this.descriptor = DescriptorParser.parseMethodDescriptor(descr);
        this.owner = owner;
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

    public JClass getOwner() {
        return owner;
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

    public MethodDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JMethod method = (JMethod) o;
        return Objects.equals(name, method.name) &&
                Objects.equals(descriptor, method.descriptor) &&
                Objects.equals(owner, method.owner) &&
                Objects.equals(exceptions, method.exceptions) &&
                Objects.equals(invocations, method.invocations);
    }

    @Override
    public int hashCode() {
        // do not use owner to avoid SOF !
        return Objects.hash(super.hashCode(), name, descriptor, exceptions, invocations);
    }

    // several useful helper methods
    public boolean isJunit4Test() {
        return !this.isPrivate() && !this.isStatic() && this.getAnnotations().stream().anyMatch(anno -> anno.getName().equals("org.junit.Test"));
    }

    public boolean isJunit5Test() {
        return !this.isPrivate() && !this.isStatic() && this.getAnnotations().stream().anyMatch(anno -> anno.getName().equals("org.junit.jupiter.api.Test"));
    }

    public boolean isConstructor() {
        return this.name.equals("<init>");
    }

    public boolean isStaticBlock() {
        return this.name.equals("<clinit>");
    }

    @Override
    public String toString() {
        return "JMethod{" + owner.getName() + "::" + name + " " + descriptor.getRawDescriptor() + '}';
    }
}
