package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.influence.maximization.algorithms.faster.GraphConversionUtilities;
import edu.iastate.research.influence.maximization.models.AlgorithmParameters;
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
    public Map<Integer, Integer> estimateNonTargetsByNode(Object graphObject, Set<String> nonTargetLabels, int noOfSimulations) {
        DirectedGraph graph = (DirectedGraph) graphObject;
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, 200);
    }

    private void generateTimDataIfNeeded(SimpleGraph graph) {
        if(TIMDataGenerated) return;

        // Time consuming tasks to be done once.
        this.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        int n = graph.getNumberOfVertices();
        int m = graph.getNumberOfEdges();
        //TODO: Get these values from somewhere else.
        int k = AlgorithmParameters.getInstance().getBudget();
        double epsilon = AlgorithmParameters.getInstance().getEpsilon();
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
        maxTargetInfluentialNodeWithTIM = this.TIMMaxInfluentialNodeMap.get(seedSet);
        List<NodeWithInfluence> maxInfluentialNodes = maxTargetInfluentialNodeWithTIM.find(graph, candidateNodes, seedSet, targetLabels, noOfSimulations);

        return maxInfluentialNodes;
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
        ++numberOfBranches;
        return maxTargetInfluentialNodeWithTIM;
    }

    @Override
    void processTreeLevel(Object graph, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, Queue<IMTreeNode> firstQueue, Queue<IMTreeNode> secondQueue, Map<Integer, Set<Integer>> nonTargetsEstimateMap, int noOfSimulations) {
        super.processTreeLevel(graph, nonTargetThreshold, targetLabels, nonTargetLabels, firstQueue, secondQueue, nonTargetsEstimateMap, noOfSimulations);

        HashMap<Integer, Set<IMTreeNode>> sharedParents = new HashMap<>();
        for (IMTreeNode treeNode :
                firstQueue) {
            Set<IMTreeNode> leaves = sharedParents.get(treeNode.getParent().getNode());
            if(leaves == null) leaves = new HashSet<>();
            leaves.add(treeNode);
            sharedParents.put(treeNode.getParent().getNode(), leaves);
        }


        // firstQueue has all the nodes expanded and pruned.
        Set<Set<Integer>> keysToNotRemove = new HashSet<>();
        for(IMTreeNode treeNode: firstQueue) {
            // Find the seed set along the path. The Hashmap is indexed by the seedSet - current leaf node.
            Set<Integer> seedSetInPath = findSeedSetInPath(treeNode);
            MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIM;
            int parentNodeID = treeNode.getParent().getNode();
            if(seedSetInPath.size()!=1) {
                Set<IMTreeNode> children = sharedParents.get(parentNodeID);
                seedSetInPath.remove(treeNode.getNode());
                //If there is more than one child, it is branching. Use a copy of the structure to account for the branching.
                if(children.size()>1) {
                    maxTargetInfluentialNodeWithTIM = this.TIMMaxInfluentialNodeMap.get(seedSetInPath).createCopy();
                } else {
                    maxTargetInfluentialNodeWithTIM = this.TIMMaxInfluentialNodeMap.get(seedSetInPath);
                }
            } else {
                //This is the first level
                maxTargetInfluentialNodeWithTIM = createMaxTargetInfluentialNodeWithTIM((SimpleGraph)graph);
            }

            //Get the max influential node calculator and add this node to the seed. Then index the Hashmap with the new seed set.
            maxTargetInfluentialNodeWithTIM.addToSeed(treeNode.getNode(), this.randomRRSetArray);
            seedSetInPath.add(treeNode.getNode());
            this.TIMMaxInfluentialNodeMap.put(seedSetInPath, maxTargetInfluentialNodeWithTIM);
            keysToNotRemove.add(seedSetInPath);
        }

        //For the nodes that branched, remove the old TIM Data so that memory if freed.
        Set<Set<Integer>> keys = new HashSet<>(this.TIMMaxInfluentialNodeMap.keySet());
        for (Set<Integer> key: keys){
            if (keysToNotRemove.contains(key)) continue;
            this.TIMMaxInfluentialNodeMap.remove(key);
        }
    }

    @Override
    protected Object getPhase2FormattedGraph(DirectedGraph graph) {
        return GraphConversionUtilities.createSimpleGraph(graph);
    }

}
