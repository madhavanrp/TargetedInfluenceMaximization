package edu.iastate.research.influence.maximization.algorithms.faster;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.WriteObject;
import edu.iastate.research.influence.maximization.algorithms.EstimateNonTargetsUsingRandomDAG;
import edu.iastate.research.influence.maximization.algorithms.NonTargetEstimator;
import edu.iastate.research.influence.maximization.models.AlgorithmParameters;
import org.apache.log4j.Logger;

import java.util.*;

public class RandomDagNTEstimator implements NonTargetEstimator {
    @Override
    public int[] estimateNonTargets(SimpleGraph graph) {
        int numberOfSimulations = AlgorithmParameters.getInstance().getNumberOfSimulations();

        for (int i = 0; i < numberOfSimulations; i++) {
            //Create DAG
            int[] dag = graph.createDag();

            //Find non targets
            //findNonTargetsInDAGWithPrunedBFS(dag, aggregatedNonTargetsMap);
        }
        return new int[0];
    }


}
