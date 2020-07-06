package nz.ac.wgtn.yamf.checks.cha;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import nz.ac.wgtn.yamf.checks.jbytecode.JMethod;

/**
 * Simple callgraph. Note that this is not a full callgraph, devirtualisation and more complex features are not
 * modelled. The idea is to model some internal logic in some project classes, plus calls to external libraries.
 * It is highly recommened to test projects using this.
 * @author jens dietrich
 */
public class CallGraph extends DirectedSparseGraph<JMethod, InvocationEdge> {
}
