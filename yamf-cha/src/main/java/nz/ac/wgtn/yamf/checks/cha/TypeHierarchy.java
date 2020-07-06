package nz.ac.wgtn.yamf.checks.cha;

import com.google.common.base.Preconditions;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import nz.ac.wgtn.yamf.checks.jbytecode.JClass;

import java.util.*;

/**
 * The type hierarchy as directed graph.
 * @author jens dietrich
 */
public class TypeHierarchy extends DirectedSparseGraph<JClass, SubtypeEdge> {

    private Map<String, JClass> verticesByName = new HashMap<>();

    JClass getClass(String name) {
        for (JClass clazz : this.getVertices()) {
            if (Objects.equals(name, clazz.getName())) {
                return clazz;
            }
        }
        return null;
    }

    @Override
    public boolean addVertex(JClass clazz) {
        boolean success = super.addVertex(clazz);
        this.verticesByName.put(clazz.getName(), clazz);
        return success;
    }

    public JClass getVertexByName(String name) {
        return this.verticesByName.get(name);
    }

    Set<JClass> getSuperTypes(JClass clazz, boolean includeThis) {
        Preconditions.checkArgument(this.containsVertex(clazz), "Type hirarchy does not contain: " + clazz);
        Set<JClass> superTypes = new HashSet<>();
        if (includeThis) {
            superTypes.add(clazz);
        }
        this.getOutEdges(clazz).stream()
                .map(e -> this.getDest(e))
                .forEach(superType -> superTypes.addAll(getSuperTypes(superType, true)));
        return superTypes;
    }
}