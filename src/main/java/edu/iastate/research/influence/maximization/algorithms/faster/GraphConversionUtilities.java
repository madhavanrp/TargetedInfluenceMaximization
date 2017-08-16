package edu.iastate.research.influence.maximization.algorithms.faster;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.models.Vertex;

public class GraphConversionUtilities {
    public static SimpleGraph createSimpleGraph(DirectedGraph directedGraph) {
        SimpleGraph graph = new SimpleGraph();
        int n = directedGraph.getNumberOfVertices();
        int m = directedGraph.getNoOfEdges();
        graph.initializeGraphStructure(n, m);
        for (Vertex u :
                directedGraph.getVertices()) {
            for (Vertex v :
                    u.getOutBoundNeighbours()) {
                graph.processEdge(u.getId(), v.getId());

            }
            boolean label = (u.getLabel().compareToIgnoreCase("A")==0);
            graph.setLabel(u.getId(), label);
        }
        return graph;
    }
}
