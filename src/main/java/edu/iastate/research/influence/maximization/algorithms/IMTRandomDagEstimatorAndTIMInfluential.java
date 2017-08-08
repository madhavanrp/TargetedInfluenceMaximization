package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class IMTRandomDagEstimatorAndTIMInfluential extends IMWithTargetLabelsWithPruning {
    private HashMap<Set<Integer>, TIMRandomRRSetMap> TIMDataStructures;
    public IMTRandomDagEstimatorAndTIMInfluential() {
        this.TIMDataStructures = new HashMap<>();

    }
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, 200);
    }

    @Override
    public List<NodeWithInfluence> findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        TIMRandomRRSetMap timStructure = this.TIMDataStructures.get(seedSet);
        if(timStructure == null) {
//            timStructure = new TIMRandomRRSetMap();
//            this.TIMDataStructures.put(seedSet, timStructure);

        }
        return new MaxTargetInfluentialNodeWithTIM(timStructure).find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }
}
