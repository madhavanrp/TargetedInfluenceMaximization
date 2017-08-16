package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.models.SimpleGraphTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomRRSetGeneratorTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void generateRandomRRSetWithLabel() throws Exception {
        SimpleGraph graph = SimpleGraphTest.getGraph();
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        int[][] rrSet = randomRRSetGenerator.generateRandomRRSetWithLabel();
        Assert.assertNotNull(rrSet);
        int[] randomSet = rrSet[0];
        boolean target = false;
        for (int i = 0; i < randomSet.length; i++) {
            int v = randomSet[i];
            if(graph.getLabel(v)) target = true;
        }
        Assert.assertTrue(target);
    }

}