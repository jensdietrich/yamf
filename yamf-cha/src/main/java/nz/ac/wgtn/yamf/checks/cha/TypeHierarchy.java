package nz.ac.wgtn.yamf.checks.cha;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Objects;

/**
 * The type hierarchy as directed graph.
 * @author jens dietrich
 */
public class TypeHierarchy extends DirectedSparseGraph<JClass, Edge> {

    JClass getClass (String name) {
        for (JClass clazz:this.getVertices()) {
            if (Objects.equals(name,clazz.getName())) {
                return clazz;
            }
        }
        return null;
    }
}
