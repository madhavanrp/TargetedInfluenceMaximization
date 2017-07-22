package edu.iastate.research.graph.models;

/**
 * Created by madhavanrp on 7/22/17.
 */
public class VertexWithFlag {
    private Vertex vertex;
    private boolean active;

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
