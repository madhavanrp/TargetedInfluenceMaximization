package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.models.SimpleGraphTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TIMRandomRRSetMapTest {
    private TIMRandomRRSetMap map;
    @Before
    public void setUp() throws Exception {
        SimpleGraph graph = SimpleGraphTest.getGraph();
        this.map = new TIMRandomRRSetMap(graph);

    }

    @After
    public void tearDown() throws Exception {
        this.map = null;
    }

    @Test
    public void removeVertexEntry() throws Exception {

    }

    @Test
    public void decrementCountForVertex() throws Exception {
        map.incrementCountForVertex(0,5);
        map.incrementCountForVertex(0,10);
        map.incrementCountForVertex(1, 10);
        map.decrementCountForVertex(0, 5);
        map.decrementCountForVertex(1, 10);
        Assert.assertEquals(1, map.countForVertex(0));
        Assert.assertEquals(0, map.countForVertex(1));

    }

    @Test
    public void incrementCountForVertex() throws Exception {
        map.incrementCountForVertex(0,5);
        map.incrementCountForVertex(1, 10);
        map.incrementCountForVertex(0,10);
        Assert.assertEquals(2, map.countForVertex(0));
        Assert.assertEquals(1, map.countForVertex(1));
    }

    @Test
    public void countForVertex() throws Exception {
    }

}