package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.models.SimpleGraphTest;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class MaxTargetInfluentialNodeWithTIMTest {

    @Before
    public void setUp() throws Exception {
        TIMRandomRRSetMap.clearLookupTable();
    }

    @Test
    public void initializeDataStructuresForTIM() throws Exception {
        SimpleGraph graph = SimpleGraphTest.getGraph();
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        int n = graph.getNumberOfVertices();
        int m = graph.getNumberOfEdges();
        int k = 40;
        double epsilon = 0.1;
        double kpt = MaxTargetInfluentialNodeWithTIM.estimateKPT(randomRRSetGenerator, n, m ,k);
        double R = MaxTargetInfluentialNodeWithTIM.calculateRValue(n, epsilon, kpt, k);
        R = 100000;
        int[][] randomRRSetArray = MaxTargetInfluentialNodeWithTIM.generateRandomRRsetArray(graph, R);
        Assert.assertEquals( Math.ceil(R), randomRRSetArray.length, 0.0);

        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM = new MaxTargetInfluentialNodeWithTIM();

        TIMRandomRRSetMap timRandomRRSetMap = new TIMRandomRRSetMap(graph);
        maxTargetInfluentialNodeWithTIM.setTimRandomRRSetMap(timRandomRRSetMap);
        maxTargetInfluentialNodeWithTIM.setGraph(graph);
        maxTargetInfluentialNodeWithTIM.setRandomRRSetArray(randomRRSetArray);
        maxTargetInfluentialNodeWithTIM.setRandomRRSetGenerator(randomRRSetGenerator);
        maxTargetInfluentialNodeWithTIM.setR(randomRRSetArray.length);
        R = maxTargetInfluentialNodeWithTIM.getR();
        MaxTargetInfluentialNodeWithTIM.incrementVertexCount(timRandomRRSetMap, randomRRSetArray, R);
        maxTargetInfluentialNodeWithTIM.initializeDataStructuresForTIM((int)Math.ceil(R));

        boolean[] nodeMark = new boolean[n];
        int[] coverage = new int[n];
        boolean[] edgeMark = new boolean[(int)Math.ceil(R)];
        System.arraycopy(timRandomRRSetMap.getNodeMark(), 0, nodeMark, 0, nodeMark.length);
        System.arraycopy(timRandomRRSetMap.getCoverage(), 0, coverage, 0, coverage.length);
        System.arraycopy(timRandomRRSetMap.getEdgeMark(), 0, edgeMark, 0, edgeMark.length);


        assertIntegerArraysAreEqual(coverage, timRandomRRSetMap.getCoverage());
        assertBooleanArraysAreEqual(nodeMark, timRandomRRSetMap.getNodeMark());
        assertBooleanArraysAreEqual(edgeMark, timRandomRRSetMap.getEdgeMark());

        Set<Integer> candidateNodes = new HashSet<>();
        for (int i = 0; i < n; i++) {
            candidateNodes.add(i);
        }
        int maxNode = maxTargetInfluentialNodeWithTIM.findNodeWithMaximumMarginalGain(candidateNodes).getNode();
        Assert.assertNotEquals(Integer.MIN_VALUE, maxNode);
        candidateNodes.remove(maxNode);
        assertIntegerArraysAreEqual(coverage, timRandomRRSetMap.getCoverage());
        assertBooleanArraysAreEqual(nodeMark, timRandomRRSetMap.getNodeMark());
        assertBooleanArraysAreEqual(edgeMark, timRandomRRSetMap.getEdgeMark());
        maxTargetInfluentialNodeWithTIM.addToSeed(maxNode, randomRRSetArray);
        Set<Integer> seedSet = new HashSet<>();
        seedSet.add(maxNode);
        assertAdditionIsCorrect(seedSet, timRandomRRSetMap, maxNode);

    }

    private void assertIntegerArraysAreEqual(int[] first, int[] second) {
        Assert.assertEquals(first.length, second.length);
        for (int i = 0; i < first.length; i++) {
            Assert.assertEquals(first[i], second[i]);
        }
    }

    private void assertBooleanArraysAreEqual(boolean[] first, boolean[] second) {
        Assert.assertEquals(first.length, second.length);
        for (int i = 0; i < first.length; i++) {
            Assert.assertEquals(first[i], second[i]);
        }
    }

    @Test
    public void testMultipleCopiesOfMaxInfluentialNode() throws Exception {
        SimpleGraph graph = SimpleGraphTest.getGraph();
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        int n = graph.getNumberOfVertices();
        int m = graph.getNumberOfEdges();
        int k = 10;
        double epsilon = 0.1;
        double kpt = MaxTargetInfluentialNodeWithTIM.estimateKPT(randomRRSetGenerator, n, m ,k);
        double R = MaxTargetInfluentialNodeWithTIM.calculateRValue(n, epsilon, kpt, k);
        R = 100000;
        int[][] randomRRSetArray = MaxTargetInfluentialNodeWithTIM.generateRandomRRsetArray(graph, R);
        MaxTargetInfluentialNodeWithTIM.incrementVertexCount(new TIMRandomRRSetMap(graph), randomRRSetArray, R);
        Assert.assertEquals( Math.ceil(R), randomRRSetArray.length, 0.0);

        int nonTargetThreshold = 10;


        List<MaxTargetInfluentialNodeWithTIM> maxTargetInfluentialNodeWithTIMList = new ArrayList<>();
        List<Set<Integer>> allSeedSets = new ArrayList<>();
        for (int i = 0; i < nonTargetThreshold+1; i++) {
            MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM = new MaxTargetInfluentialNodeWithTIM();
            TIMRandomRRSetMap timRandomRRSetMap = new TIMRandomRRSetMap(graph);
            maxTargetInfluentialNodeWithTIM.setTimRandomRRSetMap(timRandomRRSetMap);
            maxTargetInfluentialNodeWithTIM.setGraph(graph);
            maxTargetInfluentialNodeWithTIM.setRandomRRSetArray(randomRRSetArray);
            maxTargetInfluentialNodeWithTIM.setRandomRRSetGenerator(randomRRSetGenerator);
            maxTargetInfluentialNodeWithTIM.setR(randomRRSetArray.length);
            R = maxTargetInfluentialNodeWithTIM.getR();
            maxTargetInfluentialNodeWithTIM.initializeDataStructuresForTIM((int)Math.ceil(R));
            maxTargetInfluentialNodeWithTIMList.add(maxTargetInfluentialNodeWithTIM);

            allSeedSets.add(new HashSet<>());
        }

        for (int j = 0; j <k ; j++) {

            for (int i = 0; i < nonTargetThreshold + 1; i++) {
                MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM = maxTargetInfluentialNodeWithTIMList.get(i);
                Set<Integer> seedSet = allSeedSets.get(i);
                int maxNode = maxTargetInfluentialNodeWithTIM.findNodeWithMaximumMarginalGain(getRandomCandidateNodes(seedSet, graph)).getNode();
                Assert.assertFalse(seedSet.contains(maxNode));

                Assert.assertNotEquals(Integer.MIN_VALUE, maxNode);
                seedSet.add(maxNode);
                maxTargetInfluentialNodeWithTIM.addToSeed(maxNode, randomRRSetArray);
                TIMRandomRRSetMap timRandomRRSetMap = maxTargetInfluentialNodeWithTIM.getTimRandomRRSetMap();
                assertAdditionIsCorrect(seedSet, timRandomRRSetMap, maxNode);
            }
        }
//        DirectedGraph directedGraph = new FileDataReader("graph_ic.inf", 0.05f).createGraphFromData();
//        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(directedGraph, allSeedSets.get(0), 20000, new HashSet<>());
//        System.out.println("Activated set size: " + activatedSet.size());

    }

    private void assertAdditionIsCorrect(Set<Integer> seedSet, TIMRandomRRSetMap timRandomRRSetMap, int newNode) {
        boolean[] nodeMark = timRandomRRSetMap.getNodeMark();
        boolean[] edgeMark = timRandomRRSetMap.getEdgeMark();
        for (int i = 0; i < timRandomRRSetMap.getNodeMark().length; i++) {
            boolean inSeedSet = seedSet.contains(i);
            Assert.assertEquals(String.format("Contained in Seed set: %b Node Marked: %b", inSeedSet, nodeMark[i]), !inSeedSet, nodeMark[i]);

        }
        Set<Integer> edgesInfluencedBySeed = new HashSet<>();
        for (int seed :
                seedSet) {
            List<Integer> edgeInfluenceList = timRandomRRSetMap.get(seed);
            for (int edge :
                    edgeInfluenceList) {
                Assert.assertTrue("Edge should be influenced", edgeMark[edge] );
                edgesInfluencedBySeed.add(edge);
            }
        }
        for (int i = 0; i < edgeMark.length; i++) {
            if(edgesInfluencedBySeed.contains(i)) continue;
            Assert.assertFalse("Edge should not be influenced", edgeMark[i]);
        }

        int[] coverage = timRandomRRSetMap.getCoverage();
        for (int i = 0; i < timRandomRRSetMap.getNodeMark().length; i++) {
            //Don't test if seed set has this node - Coverage doesn't matter if it is??
            if(seedSet.contains(i)) continue;
            int c = coverage[i];
            if(timRandomRRSetMap.get(i) == null) {
                Assert.assertEquals(0, c);
            } else {
                Set<Integer> edgesInfluencedByThisNode = new HashSet<>(timRandomRRSetMap.get(i));
                edgesInfluencedByThisNode.removeAll(edgesInfluencedBySeed);
                Assert.assertEquals(edgesInfluencedByThisNode.size(), c);
            }
        }
    }

    private static int numberChecked = 0;

    private Set<Integer> getRandomCandidateNodes(Set<Integer> seedSet, SimpleGraph graph) {
        Set<Integer> candidateNodes = new HashSet<>();
        for (int i = 0; i < graph.getNumberOfVertices(); i++) {
            boolean add = ThreadLocalRandom.current().nextBoolean();
            if(seedSet!=null && seedSet.contains(i)) continue;
            if(add) {
                candidateNodes.add(i);
            }
        }
        return candidateNodes;
    }
    @Test
    public void findNodeWithMaximumMarginalGain() throws Exception {
    }

    @Test
    public void addToSeed() throws Exception {
    }

}