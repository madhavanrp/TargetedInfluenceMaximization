package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.Vertex;

import java.util.Set;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class RandomRRSet {
    public Set<Vertex> randomRRSet;
    public int width;
    public RandomRRSet(Set<Vertex> randomRRSet, int width) {
        this.randomRRSet = randomRRSet;
        this.width = width;
    }
}
