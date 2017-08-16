package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;

public class IMTEfficient {

    private NonTargetEstimator estimator;
    private int[] nonTargetMap;
    private SimpleGraph graph;

    public IMTEfficient(SimpleGraph graph, NonTargetEstimator estimator) {
        this.estimator = estimator;
        this.graph = graph;
    }

    public void estimateNonTargets() {
        this.nonTargetMap = estimator.estimateNonTargets(this.graph);
    }
}
