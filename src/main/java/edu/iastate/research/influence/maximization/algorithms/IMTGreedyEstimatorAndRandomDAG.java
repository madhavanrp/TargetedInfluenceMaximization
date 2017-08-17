package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNodeWithRandomDAG.getInstance;

/**
 * Created by Naresh on 1/23/2017.
 */
public class IMTGreedyEstimatorAndRandomDAG extends IMWithTargetLabelsWithPruning {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(Object graphObject, Set<String> nonTargetLabels, int noOfSimulations) {
        DirectedGraph graph = (DirectedGraph) graphObject;
        return new EstimateNonTargetsUsingGreedy().estimate(graph, nonTargetLabels, noOfSimulations);
    }

    @Override
    public List<NodeWithInfluence> findMaxInfluentialNode(Object graphObject, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        DirectedGraph graph = (DirectedGraph)graphObject;
        return getInstance(graph, targetLabels, noOfSimulations).find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }
}
