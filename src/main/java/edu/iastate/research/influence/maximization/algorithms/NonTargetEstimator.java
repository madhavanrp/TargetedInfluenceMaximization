package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;

public interface NonTargetEstimator {
    // This should give the number of non targets influenced by each vertex.
    int[] estimateNonTargets(SimpleGraph graph);
}
