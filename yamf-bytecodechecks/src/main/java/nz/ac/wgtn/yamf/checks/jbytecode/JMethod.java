package nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.checks.jbytecode.descr.DescriptorParser;
import nz.ac.wgtn.yamf.checks.jbytecode.descr.MethodDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    public JMethod(String name, String descr, String[] excepts, int modifiers) {
        super(modifiers);
        this.name = name;
        this.descriptor = DescriptorParser.parseMethodDescriptor(descr);
        this.exceptions = excepts==null
            ? Collections.EMPTY_LIST
            : Stream.of(excepts).map(x -> x.replace("/",".")).collect(Collectors.toList());
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
}
