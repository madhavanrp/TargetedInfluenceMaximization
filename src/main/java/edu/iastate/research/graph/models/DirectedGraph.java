package edu.iastate.research.graph.models;

import edu.iastate.research.graph.utilities.FileDataReader;

import java.io.*;
import java.util.*;

import static org.apache.log4j.NDC.remove;

/**
 * Created by Naresh on 2/23/2016.
 */
public class DirectedGraph implements Serializable {

    private Map<Integer, Vertex> vertexMap = new HashMap();

    /**
     * Getter for property 'vertices'.
     *
     * @return Value for property 'vertices'.
     */
    public Set<Vertex> getVertices() {
        return vertices;
    }

    Set<Vertex> vertices;
    int noOfEdges;
    private Vertex maxDegreeVertex;

    public Vertex getMaxDegreeVertex() {
        return maxDegreeVertex;
    }


    public DirectedGraph() {
        vertices = new HashSet<>();
        this.noOfEdges = 0;

    }

    /**
     * Getter for property 'noOfEdges'.
     *
     * @return Value for property 'noOfEdges'.
     */
    public int getNoOfEdges() {
        return noOfEdges;
    }

    public int getNumberOfVertices() {
        return this.vertices.size();
    }

    public void addVertex(Vertex v) {
        this.vertices.add(v);
        vertexMap.put(v.getId(), v);
    }

    public void addVertex(int value) {
        this.vertices.add(new Vertex(value));
    }

    public Vertex find(int id) {
        return vertexMap.get(id);
    }

    public void addEdge(int from, int to, float propagationProbability) {
        Vertex fromVertex = find(from);
        Vertex toVertex = find(to);
        if (fromVertex == null) {
            fromVertex = new Vertex(from);
            this.vertices.add(fromVertex);
            vertexMap.put(fromVertex.getId(), fromVertex);
        }
        if (toVertex == null) {
            toVertex = new Vertex(to);
            this.vertices.add(toVertex);
            vertexMap.put(toVertex.getId(), toVertex);
        }
        fromVertex.addOutBoundNeighbour(toVertex, propagationProbability);
        toVertex.addInBoundNeighbour(fromVertex);
        if(this.maxDegreeVertex == null) {
            this.maxDegreeVertex = fromVertex;
        }
        if(fromVertex.getDegree()> this.maxDegreeVertex.getDegree()) {
            this.maxDegreeVertex = fromVertex;
        }
        if(toVertex.getDegree()> this.maxDegreeVertex.getDegree()) {
            this.maxDegreeVertex = toVertex;
        }
        noOfEdges++;
    }

    public void addEdge(Vertex from, Vertex to, float propagationProbability) {
        Vertex fromVertex = find(from.getId());
        Vertex toVertex = find(to.getId());
        if (fromVertex == null) {
            fromVertex = new Vertex(from.getId());
            fromVertex.setLabel(from.getLabel());
            this.vertices.add(fromVertex);
            vertexMap.put(fromVertex.getId(), fromVertex);
        }
        if (toVertex == null) {
            toVertex = new Vertex(to.getId());
            toVertex.setLabel(to.getLabel());
            this.vertices.add(toVertex);
            vertexMap.put(toVertex.getId(), toVertex);
        }
        addEdge(from.getId(), to.getId(), propagationProbability);
    }

    public void print() {
        System.out.print("Vertices of the graph : ");
        StringBuilder vertexString = new StringBuilder("");
        StringBuilder edgeString = new StringBuilder("");
        for (Vertex vertex : vertices) {
            vertexString.append(vertex.getId() + ",");
            edgeString.append("Edges for the vertex " + vertex.getId() + " are : ");
            for (Vertex neighbour : vertex.getOutBoundNeighbours()) {
                edgeString.append(neighbour.getId() + ",");
            }
            edgeString.deleteCharAt(edgeString.length() - 1);
            edgeString.append("\n");
        }

        System.out.println(vertexString.deleteCharAt(vertexString.length() - 1));
        System.out.println(edgeString);
    }

    public DirectedGraph copyVertices() {
        DirectedGraph graph = new DirectedGraph();
        for (Vertex vertex : vertices) {
            Vertex clonedVertex =new Vertex(vertex.getId());
            clonedVertex.setLabel(vertex.getLabel());
            graph.addVertex(clonedVertex);
        }
        return graph;
    }

    public void removeEdge(Vertex from, Vertex to) {
        from.removeOutBoundNeighbour(to);
        to.removeInBoundNeighbour(from);
    }

    public void randomizeDag() {
        for (Vertex v : this.getVertices()) {
            for (Vertex vOut : v.getOutBoundNeighbours()) {
                boolean active = !(new Random().nextFloat() < (1 - v.getPropagationProbability(vOut)));
            }
        }
    }

    public Set<Vertex> findAncestors(Vertex vertex) {
        Set<Vertex> reachableVertices = new HashSet<>();
        Queue<Vertex> queue = new LinkedList();
        queue.add(vertex);
        while (!queue.isEmpty()) {
            Vertex u = queue.remove();
            if(reachableVertices.contains(u)) continue;
            reachableVertices.add(u);
            for (Vertex v:
                    u.getInBoundNeighbours()) {
                queue.add(v);
            }
        }
        reachableVertices.remove(vertex);
        return reachableVertices;
    }

    public Set<Vertex> findDescendants(Vertex vertex) {
        Set<Vertex> reachableVertices = new HashSet<>();
        Queue<Vertex> queue = new LinkedList();
        queue.add(vertex);
        while (!queue.isEmpty()) {
            Vertex u = queue.remove();
            if(reachableVertices.contains(u)) continue;
            reachableVertices.add(u);
            for (Vertex v:
                 u.getOutBoundNeighbours()) {
                queue.add(v);
            }
        }
        return reachableVertices;
    }

    public Set<Vertex> getVerticesWithLabel(String label) {
        Set<Vertex> labelledVertices = new HashSet<>();
        for (Vertex vertex:
             this.getVertices()) {
            if(vertex.getLabel().compareToIgnoreCase(label)==0) {
                labelledVertices.add(vertex);
            }
        }
        return labelledVertices;
    }


    public void scc() {
        Stack stack = new Stack();
        Set<Vertex> visited = new HashSet<>();


        for (Vertex vertex :
                vertices) {
            if(!visited.contains(vertex)) {
                fillOrder(vertex, visited, stack);
            }
        }

        visited = new HashSet<>();

        while(!stack.isEmpty()) {
            Vertex vertex = (Vertex)stack.pop();
            if(!visited.contains(vertex)) {
                DFSUtil(vertex, visited);
//                System.out.println();
                numberSCC++;
            }
        }
    }
    public int numberSCC = 0;
    private void DFSUtil(Vertex vertex, Set<Vertex> visited) {
        visited.add(vertex);
//        System.out.print(vertex.getId() + " ");

        Queue<Vertex> queue = new LinkedList<>(vertex.getInBoundNeighbours());
        while(!queue.isEmpty()) {
            Vertex v = queue.remove();
            if(!visited.contains(v)) {
                DFSUtil(v, visited);
            }
        }
    }

    private void fillOrder(Vertex vertex, Set<Vertex> visited, Stack stack) {
        visited.add(vertex);
        Queue<Vertex> queue = new LinkedList<>(vertex.getOutBoundNeighbours());
        while(!queue.isEmpty()) {
            Vertex v = queue.remove();
            if(!visited.contains(v)) fillOrder(v, visited, stack);

        }
        stack.push(vertex);

    }

    public static void main(String[] args) {
        DirectedGraph g;
        FileDataReader fileDataReader = new FileDataReader("graph_ic.inf", 0.01f);
        g = fileDataReader.createGraphFromData();
        System.out.println("Vertices " + g.getNumberOfVertices());
        System.out.println("Edges " + g.getNoOfEdges());
    }

    public void writeGraphToFile(String fileName) {
        try {
            File fout = new File("out.txt");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (Vertex u :
                    this.getVertices()) {
                for (Vertex v :
                        u.getOutBoundNeighbours()) {
                    bw.write(String.format("%d %d %f", u.getId(), v.getId(), u.getPropagationProbability(v)));
                    bw.newLine();
                }
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
