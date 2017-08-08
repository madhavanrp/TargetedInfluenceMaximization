package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class RandomRRSetGenerator {
    private Object[] graph;
    private int[] inDegree;
    public RandomRRSetGenerator(Object[] graph, int[] inDegree) {
        this.graph = graph;
        this.inDegree = inDegree;
    }

    public int[][] generateRandomRRSet() {
         return generateRandomRRSetWithLabel(null);
    }

    public int[][] generateRandomRRSetWithLabel(String label) {
        int randomVertex = -1;
        while(randomVertex==-1) {
            int i = ThreadLocalRandom.current().nextInt(0, this.graph.length);
            randomVertex = i;
        }

        Queue<Integer> queue = new LinkedList<>();
        queue.add(randomVertex);
        Set<Integer> randomRRSet= new HashSet<>();
        int width = 0;
        while (!queue.isEmpty()) {
            int u = queue.remove();
            if(randomRRSet.contains(u)) continue;
            randomRRSet.add(u);
            //TODO: Change width
            width+= inDegree[u];
            for (Integer v :
                    (List<Integer>)this.graph[u]) {
                if (!(ThreadLocalRandom.current().nextFloat() < (1 - 1f/Float.valueOf(inDegree[v])))) {
                    if(!randomRRSet.contains(v))
                        queue.add(v);
                }
            }
        }
        //Using this structure instead of a class to attempt to use less memory
        int[][] randomRRSet2DArray = new int[3][];
        // Set the ID
        randomRRSet2DArray[0] = new int[1];
        randomRRSet2DArray[1] = new int[randomRRSet.size()];
        int i = 0;
        for (Integer setID :
                randomRRSet) {
            randomRRSet2DArray[1][i++] = setID;
        }
        randomRRSet2DArray[2] = new int[1];
        randomRRSet2DArray[2][0] = width;

        return randomRRSet2DArray;
    }
}
