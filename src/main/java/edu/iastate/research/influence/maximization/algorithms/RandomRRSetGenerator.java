package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class RandomRRSetGenerator {
    private List<Vertex> vertices;
    public RandomRRSetGenerator(DirectedGraph graph) {
        this.vertices = new ArrayList<>(graph.getVertices());
    }

    public int[][] generateRandomRRSet() {
         return generateRandomRRSetWithLabel(null);
    }

    public int[][] generateRandomRRSetWithLabel(String label) {
        Vertex randomVertex = null;
        while(randomVertex==null) {
            int i = ThreadLocalRandom.current().nextInt(0, this.vertices.size());
            randomVertex = this.vertices.get(i);
            if(label!=null && (randomVertex.getLabel().compareToIgnoreCase(label)!=0)) {
                randomVertex = null;
            }
        }

        Queue<Vertex> queue = new LinkedList<>();
        queue.add(randomVertex);
        Set<Integer> randomRRSet= new HashSet<>();
        int width = 0;
        while (!queue.isEmpty()) {
            Vertex u = queue.remove();
            if(randomRRSet.contains(u.getId())) continue;
            randomRRSet.add(u.getId());
            width+= u.getIndDegree();
            for (Vertex v :
                    u.getInBoundNeighbours()) {
                if (!(ThreadLocalRandom.current().nextFloat() < (1 - v.getPropagationProbability(u)))) {
                    queue.add(v);
                }
            }
        }
        width = ThreadLocalRandom.current().nextInt(0,50);
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
