package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.Vertex;

import java.util.Set;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class RandomRRSet {
    public Set<Integer> randomRRSet;
    public int width;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id;
    public RandomRRSet(Set<Integer> randomRRSet, int width) {
        this.randomRRSet = randomRRSet;
        this.width = width;
    }
}
