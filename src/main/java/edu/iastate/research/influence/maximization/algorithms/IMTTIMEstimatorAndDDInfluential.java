package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.influence.maximization.algorithms.faster.GraphConversionUtilities;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IMTTIMEstimatorAndDDInfluential extends IMWithTargetLabelsWithPruning{
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(Object graphObject, Set<String> nonTargetLabels, int noOfSimulations) {
        SimpleGraph graph = (SimpleGraph)graphObject;
        EstimateNonTargetsUsingTIM estimateNonTargetsUsingTIM = new EstimateNonTargetsUsingTIM();
        return estimateNonTargetsUsingTIM.estimate(graph, nonTargetLabels, noOfSimulations);
    }

    @Override
    public List<NodeWithInfluence> findMaxInfluentialNode(Object graphObject, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        DirectedGraph graph = (DirectedGraph)graphObject;
        return new MaxTargetInfluentialNodeWithDegreeDiscount().find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }

    @Override
    protected Object getPhase1FormattedGraph(DirectedGraph graph) {
        return GraphConversionUtilities.createSimpleGraph(graph);
    }
}
