package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 1/17/2017.
 */
public class IMTRandomDAGEstimatorAndDegreeDiscount extends IMWithTargetLabelsWithPruning {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, 20000);
    }

    @Override
    public List<NodeWithInfluence> findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return new MaxTargetInfluentialNodeWithDegreeDiscount().find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }
}
