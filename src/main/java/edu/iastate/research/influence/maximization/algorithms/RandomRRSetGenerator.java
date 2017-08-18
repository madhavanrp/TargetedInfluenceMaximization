package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.SimpleGraph;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class RandomRRSetGenerator {
    private SimpleGraph graph;
    private int n;
    private int inDegree[];
    private int[][] graphTranspose;

    public RandomRRSetGenerator(SimpleGraph simpleGraph) {
        this.graph = simpleGraph;
        this.n = simpleGraph.getNumberOfVertices();
        this.graphTranspose = simpleGraph.getGraphTranspose();
        this.inDegree = simpleGraph.getInDegree();
    }

    public int[][] generateRandomRRSetWithLabel() {
        return generateRandomRRSetWithLabel(true);
    }

    public int[][] generateRandomRRSetWithLabel(boolean label) {
        int random = ThreadLocalRandom.current().nextInt(0, this.n);
        while(this.graph.getLabel(random)!=label) {
            random = ThreadLocalRandom.current().nextInt(0, this.n);
        }
        return generateRandomRRSetStartingFrom(random);
    }

    public int[][] generateRandomRRSetFromSubset(int[] nodes) {
        int randomNodeIndex = ThreadLocalRandom.current().nextInt(0, nodes.length);
        int randomNode = nodes[randomNodeIndex];
        return generateRandomRRSetStartingFrom(randomNode);
    }

    private int[][] generateRandomRRSetStartingFrom(int random) {
        int width = 0;
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(random);
        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            if (visited.contains(vertex)) continue;
            visited.add(vertex);
            int[] incomingVertices = this.graphTranspose[vertex];
            for (int incoming :
                    incomingVertices) {
                width++;
                if (visited.contains(incoming)) continue;
                float p = ThreadLocalRandom.current().nextFloat();
                float propogationProbability = Float.valueOf(1) / Float.valueOf(this.inDegree[vertex]);
                if (p > propogationProbability) continue;
                queue.add(incoming);
            }
        }
        int[] rrSet = new int[visited.size()];
        int j = 0;
        for (int v : visited) {
            rrSet[j++] = v;
        }

        int[][] rrSetData = new int[2][];
        rrSetData[0] = rrSet;
        rrSetData[1] = new int[]{width};
        return rrSetData;
    }


}
