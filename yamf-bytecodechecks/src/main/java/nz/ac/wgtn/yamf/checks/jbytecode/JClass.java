package nz.ac.wgtn.yamf.checks.jbytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Simple representation of a class.
 * @author jens dietrich
 */
public class JClass extends JArtifact {

    private String name = null;
    private String superClass = null;
    private List<String> interfaces = new ArrayList<>();
    private List<JField> fields = new ArrayList<>();
    private List<JMethod> methods = new ArrayList<>();

    public JClass(int modifiers,String name) {
        super(modifiers);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public List<String> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    void addInterface(String itf) {
        this.interfaces.add(itf);
    }

    public List<JField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    void addField(JField field) {
        this.fields.add(field);
    }

    public List<JMethod> getMethods() {
        return methods;
    }

    void addMethod(JMethod method) {
        this.methods.add(method);
    }

    public boolean hasDefaultConstructor() {
        return this.getMethods().stream()
            .filter(method -> method.isPublic())
            .filter(method -> method.getName().equals("<init>"))
            .filter(method -> method.getReturnType().equals("void"))
            .anyMatch(method -> method.getParameterTypes().isEmpty());
    }

    public boolean hasGetter(String property,String type) {
        String methodName = null;
        if (type.equals("boolean")) {
            methodName = "is" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        else {
            methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        String methodName2 = methodName; // to make final for lambda
        return this.getMethods().stream()
            .filter(method -> method.isPublic())
            .filter(method -> !method.isStatic())
            .filter(method -> method.getName().equals(methodName2))
            .filter(method -> method.getReturnType().equals(type))
            .anyMatch(method -> method.getParameterTypes().isEmpty());
    }

    public boolean hasSetter(String property,String type) {
        String methodName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        return this.getMethods().stream()
            .filter(method -> method.isPublic())
            .filter(method -> !method.isStatic())
            .filter(method -> method.getName().equals(methodName))
            .filter(method -> method.getReturnType().equals("void"))
            .filter(method -> method.getParameterTypes().size()==1)
            .anyMatch(method -> method.getParameterTypes().isEmpty());
    }

    public boolean hasGetterAndSetter(String property,String type) {
        String methodName = null;
        if (type.equals("boolean")) {
            methodName = "is" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        else {
            methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        String methodName2 = methodName; // to make final for lambda
        return this.getMethods().stream()
                .filter(method -> method.isPublic())
                .filter(method -> !method.isStatic())
                .filter(method -> method.getName().equals(methodName2))
                .filter(method -> method.getReturnType().equals(type))
                .anyMatch(method -> method.getParameterTypes().isEmpty());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JClass jClass = (JClass) o;
        return Objects.equals(name, jClass.name) &&
                Objects.equals(superClass, jClass.superClass) &&
                Objects.equals(interfaces, jClass.interfaces) &&
                Objects.equals(fields, jClass.fields) &&
                Objects.equals(methods, jClass.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, superClass, interfaces, fields, methods);
    }
}
