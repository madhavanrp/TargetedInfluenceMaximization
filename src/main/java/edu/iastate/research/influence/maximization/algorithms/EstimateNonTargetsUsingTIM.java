package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.utilities.WriteObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EstimateNonTargetsUsingTIM extends EstimateNonTargets{
    final static Logger logger = Logger.getLogger(EstimateNonTargetsUsingTIM.class);

    @Override
    public Map<Integer, Integer> estimate(Object graphObject, Set<String> nonTargetLabels, int noOfSimulations) {
        SimpleGraph graph = (SimpleGraph) graphObject;
        int[][] graphTranspose = graph.getGraphTranspose();
        int[] inDegree = graph.getInDegree();
        int[] nonTargets = graph.getNonTargetNodes();
        int[] nodeCounts = new int[graph.getNumberOfVertices()];
        int R = 1000000;


        for (int i = 0; i < R; i++) {
            Set<Integer> visited = new HashSet<>();
            Queue<Integer> queue = new LinkedList<>();
            int randomIndex = ThreadLocalRandom.current().nextInt(0, nonTargets.length);
            int randomNode = nonTargets[randomIndex];
            queue.add(randomNode);
            while (!queue.isEmpty()) {
                int vertex = queue.remove();
                if (visited.contains(vertex)) continue;
                visited.add(vertex);
                nodeCounts[vertex]++;
                int[] incomingVertices = graphTranspose[vertex];
                for (int incoming :
                        incomingVertices) {
                    if (visited.contains(incoming)) continue;
                    float p = ThreadLocalRandom.current().nextFloat();
                    float propogationProbability = Float.valueOf(1) / Float.valueOf(inDegree[vertex]);
                    if (p > propogationProbability) continue;
                    queue.add(incoming);
                }
            }
        }
        HashMap<Integer, Integer> nonTargetsMap = new HashMap<>();
        for (int i = 0; i < graph.getNumberOfVertices(); i++) {
            float nonTargetsActivatedEstimate = (float) nodeCounts[i] * nonTargets.length/R;
            nonTargetsMap.put(i, (int)Math.round(nonTargetsActivatedEstimate));
        }

        String filename = UUID.randomUUID().toString() + "-non-targets-map.data";
        logger.info("Writing Estimated Non Targets Map to file " + filename);
        WriteObject.writeToFile(nonTargetsMap, filename);
        return nonTargetsMap;
    }
}
