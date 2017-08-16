package edu.iastate.research.influence.maximization.algorithms;

import com.sun.tools.javac.util.Assert;
import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.influence.maximization.algorithms.faster.GraphConversionUtilities;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.*;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class IMTRandomDagEstimatorAndTIMInfluential extends IMWithTargetLabelsWithPruning {
    private int[][] randomRRSetArray = null;
    private RandomRRSetGenerator randomRRSetGenerator = null;
    private TIMRandomRRSetMap timRandomRRSetMap;
    private HashMap<Set<Integer>, MaxTargetInfluentialNodeWithTIM> TIMMaxInfluentialNodeMap;
    private boolean TIMDataGenerated = false;
    public IMTRandomDagEstimatorAndTIMInfluential() {
        this.TIMMaxInfluentialNodeMap = new HashMap<>();
    }
    private static int numberOfBranches = -1;

    public IMTRandomDagEstimatorAndTIMInfluential(DirectedGraph graph) {
        // Maybe convert the graph here.
    }

    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, 200);
    }

    private void generateTimDataIfNeeded(SimpleGraph graph) {
        if(TIMDataGenerated) return;

        // Time consuming tasks to be done once.
        this.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        int n = graph.getNumberOfVertices();
        int m = graph.getNumberOfEdges();
        //TODO: Get these values from somewhere else.
        int k = 40;
        double epsilon = 0.1;
        double kpt = MaxTargetInfluentialNodeWithTIM.estimateKPT(this.randomRRSetGenerator, n, m ,k);
        double R = MaxTargetInfluentialNodeWithTIM.calculateRValue(n, epsilon, kpt, k);
        int[][] randomRRSetArray = MaxTargetInfluentialNodeWithTIM.generateRandomRRsetArray(graph, R);
        this.randomRRSetArray = randomRRSetArray;
        this.timRandomRRSetMap = new TIMRandomRRSetMap(graph);
        MaxTargetInfluentialNodeWithTIM.incrementVertexCount(this.timRandomRRSetMap, randomRRSetArray, R);
        TIMDataGenerated = true;

        this.TIMMaxInfluentialNodeMap.put(new HashSet<>(), createMaxTargetInfluentialNodeWithTIM(graph));
    }
    @Override
    public List<NodeWithInfluence> findMaxInfluentialNode(Object graphObject, Set<Integer> candidateNodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        SimpleGraph graph = (SimpleGraph)graphObject;
        this.generateTimDataIfNeeded(graph);
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM;
//        if (seedSet!=null && !seedSet.isEmpty()) {
            maxTargetInfluentialNodeWithTIM = this.TIMMaxInfluentialNodeMap.get(seedSet);
//        }
//        else {
//            System.out.println("Seed set size: " + seedSet.size());
//            maxTargetInfluentialNodeWithTIM = createMaxTargetInfluentialNodeWithTIM(graph);
//
//        }
        return maxTargetInfluentialNodeWithTIM.find(graph, candidateNodes, seedSet, targetLabels, noOfSimulations);
    }

    private MaxTargetInfluentialNodeWithTIM createMaxTargetInfluentialNodeWithTIM(SimpleGraph graph) {
        //Use previously generated values of TIM data to create the object.
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM = new MaxTargetInfluentialNodeWithTIM();
        maxTargetInfluentialNodeWithTIM.setTimRandomRRSetMap(this.timRandomRRSetMap);
        maxTargetInfluentialNodeWithTIM.setGraph(graph);
        maxTargetInfluentialNodeWithTIM.setRandomRRSetArray(this.randomRRSetArray);
        maxTargetInfluentialNodeWithTIM.setRandomRRSetGenerator(this.randomRRSetGenerator);
        maxTargetInfluentialNodeWithTIM.setR(this.randomRRSetArray.length);
        double R = maxTargetInfluentialNodeWithTIM.getR();
        maxTargetInfluentialNodeWithTIM.initializeDataStructuresForTIM((int)Math.ceil(R));
        System.out.println(String.format("Creating %d branch", ++numberOfBranches));
        return maxTargetInfluentialNodeWithTIM;
    }

    @Override
    void processTreeLevel(Object graph, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, Queue<IMTreeNode> firstQueue, Queue<IMTreeNode> secondQueue, Map<Integer, Set<Integer>> nonTargetsEstimateMap, int noOfSimulations) {
        super.processTreeLevel(graph, nonTargetThreshold, targetLabels, nonTargetLabels, firstQueue, secondQueue, nonTargetsEstimateMap, noOfSimulations);

        HashMap<Integer, MaxTargetInfluentialNodeWithTIM> newBranchInfluentialCalculator = new HashMap<>();
        //First identify the new branches
        for (IMTreeNode treeNode: firstQueue) {
            int leafNodePathID = treeNode.getPathID();
            int parentNodePathID = treeNode.getParent().getPathID();
            if(leafNodePathID==parentNodePathID) continue;

            //Create a copy
            System.out.println("Spawning new branch " + leafNodePathID);
        }
        // firstQueue has all the nodes expanded and pruned.
        for(IMTreeNode treeNode: firstQueue) {
            // Find the seed set along the path. The Hashmap is indexed by the seedSet - current leaf node.
            Set<Integer> seedSetInPath = findSeedSetInPath(treeNode);
            MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM;
            int leafNodePathID = treeNode.getPathID();
            int parentNodePathID = treeNode.getParent().getPathID();
            if(seedSetInPath.size()!=1) {
                // If it is branching. i.e a new path has to be generated, Create a copy of TIM MaxInfluential Node
//                if(leafNodePathID!=parentNodePathID) {
//                    maxTargetInfluentialNodeWithTIM
//                }
                seedSetInPath.remove(treeNode.getNode());
                maxTargetInfluentialNodeWithTIM = this.TIMMaxInfluentialNodeMap.get(seedSetInPath);
            } else {
                maxTargetInfluentialNodeWithTIM = createMaxTargetInfluentialNodeWithTIM((SimpleGraph)graph);

            }

            //Get the max influential node calculator and add this node to the seed. Then index the Hashmap with the new seed set.
            maxTargetInfluentialNodeWithTIM.addToSeed(treeNode.getNode(), this.randomRRSetArray);
            seedSetInPath.add(treeNode.getNode());
            this.TIMMaxInfluentialNodeMap.put(seedSetInPath, maxTargetInfluentialNodeWithTIM);
        }
    }

    @Override
    protected Object getPhase2FormattedGraph(DirectedGraph graph) {
        return GraphConversionUtilities.createSimpleGraph(graph);
    }
}
