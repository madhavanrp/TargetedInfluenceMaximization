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

    public RandomRRSet generateRandomRRSet() {
         return generateRandomRRSetWithLabel(null);
    }

    public RandomRRSet generateRandomRRSetWithLabel(String label) {
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
        RandomRRSet randomRRset = new RandomRRSet(randomRRSet, width);
        return randomRRset;
    }
}
