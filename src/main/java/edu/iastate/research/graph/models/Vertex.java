package edu.iastate.research.graph.models;

import java.io.Serializable;
import java.util.*;


/**
 * Created by Naresh on 2/23/2016.
 */
public class Vertex implements Serializable {

    private int id;
    private int indDegree;
    private int outDegree;
    private List<Vertex> inBoundNeighbours;
    private List<Vertex> outBoundNeighbours;
    String label;

    public Vertex(int id) {
        this.id = id;
        this.inBoundNeighbours = new ArrayList<>();
        this.outBoundNeighbours = new ArrayList<>();
        this.indDegree = 0;
        this.outDegree = 0;
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for property 'indDegree'.
     *
     * @return Value for property 'indDegree'.
     */
    public int getIndDegree() {
        return indDegree;
    }

    /**
     * Setter for property 'indDegree'.
     *
     * @param indDegree Value to set for property 'indDegree'.
     */
    public void setIndDegree(int indDegree) {
        this.indDegree = indDegree;
    }

    /**
     * Getter for property 'outDegree'.
     *
     * @return Value for property 'outDegree'.
     */
    public int getOutDegree() {
        return outDegree;
    }

    /**
     * Setter for property 'outDegree'.
     *
     * @param outDegree Value to set for property 'outDegree'.
     */
    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    /**
     * Getter for property 'inBoundNeighbours'.
     *
     * @return Value for property 'inBoundNeighbours'.
     */
    public Collection<Vertex> getInBoundNeighbours() {
        return inBoundNeighbours;
    }



    /**
     * Getter for property 'outBoundNeighbours'.
     *
     * @return Value for property 'outBoundNeighbours'.
     */
    public Collection<Vertex> getOutBoundNeighbours() {
        return outBoundNeighbours;
    }

    public float getPropagationProbability(Vertex neighbour) {
        return 0.05f;
    }

    public int getDegree() {
        return indDegree + outDegree;
    }

    public void addInBoundNeighbour(Vertex v) {
        this.inBoundNeighbours.add(v);
        indDegree++;
    }

    public void addOutBoundNeighbour(Vertex vertexWithFlag, float propagationProbability) {
        this.outBoundNeighbours.add(vertexWithFlag);
        this.outDegree++;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    public void removeOutBoundNeighbour(Vertex toVertex) {
        this.outBoundNeighbours.remove(toVertex);
        outDegree--;
    }

    public void removeInBoundNeighbour(Vertex fromVertex) {
        this.inBoundNeighbours.remove(fromVertex);
        indDegree--;
    }

    public boolean hasLabel(Set<String> labels) {
        for (String l :
                labels) {
            if(l.compareToIgnoreCase(this.label)==0) return true;
        }
        return false;
    }

}
