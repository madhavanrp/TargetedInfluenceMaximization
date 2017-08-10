package edu.iastate.research.graph.models;

import edu.iastate.research.graph.models.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

public class SimpleGraphTest {
    public static SimpleGraph getGraph() {
        URL url = SimpleGraphTest.class.getClassLoader().getResource("graph_ic.inf");
        SimpleGraph graph = SimpleGraph.fromFile(url.getPath());
        return graph;
    }
    @Test
    public void testReadGraph() {
        SimpleGraph graph = getGraph();
        int n = graph.getNumberOfVertices();
        int m = graph.getNumberOfEdges();
        Assert.assertEquals(15229, n);
        Assert.assertEquals(62752, m);
    }

    @Test
    public void testTranspose() {
        SimpleGraph graph = getGraph();
        int[][] original = graph.getGraph();
        int[][] transpose = graph.getGraphTranspose();

        for (int i = 0; i < graph.getNumberOfVertices(); i++) {
            for (int j = 0; j < original[i].length; j++) {
                boolean transposeEdgeExists = false;
                int edgeTo = original[i][j];
                for (int k = 0; k < transpose[edgeTo].length; k++) {
                    if(transpose[edgeTo][k]==i) {
                        transposeEdgeExists = true;
                    }
                }
                Assert.assertTrue(transposeEdgeExists);
            }
        }
    }
}
