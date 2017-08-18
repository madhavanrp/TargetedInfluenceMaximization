package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.algorithms.faster.GraphConversionUtilities;

import java.util.Map;
import java.util.Set;

public class IMTTIMEstimatorAndTIMInfluential extends IMTRandomDagEstimatorAndTIMInfluential {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(Object graph, Set<String> nonTargetLabels, int noOfSimulations) {
        EstimateNonTargetsUsingTIM estimateNonTargetsUsingTIM = new EstimateNonTargetsUsingTIM();
        return estimateNonTargetsUsingTIM.estimate(graph, nonTargetLabels, noOfSimulations);
    }

    @Override
    protected Object getPhase1FormattedGraph(DirectedGraph graph) {
        return GraphConversionUtilities.createSimpleGraph(graph);
    }
}
