package nz.ac.wgtn.yamf.checks.jbytecode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents annotations.
 * @author jens dietrich
 */
public class JAnnotation extends JArtifact {

    private String name = null;
    private Map<String,Object> properties = new HashMap<>();

    public JAnnotation(int modifiers,String name) {
        super(modifiers);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public Object setProperty(String propertyName,Object value) {
        return properties.put(propertyName,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JAnnotation that = (JAnnotation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, properties);
    }
}
