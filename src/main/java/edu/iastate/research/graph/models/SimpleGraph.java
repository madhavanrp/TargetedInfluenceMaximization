package edu.iastate.research.graph.models;

import java.io.*;
import java.util.Arrays;

public class SimpleGraph {
    private int[][] graph;
    private int[][] graphTranspose;
    private int n;
    private int m;
    private int inDegree[];

    public static SimpleGraph fromFile(String filePath, boolean buildTranspose) {
        SimpleGraph graph = new SimpleGraph();
        graph.readGraph(filePath);
        return graph;
    }

    public int getNumberOfEdges() {
        return m;
    }

    public int getNumberOfVertices() {
        return n;
    }

    public int[][] getGraph() {
        return graph;
    }

    public int[][] getGraphTranspose() {
        return graphTranspose;
    }

    public int[] getInDegree() {
        return inDegree;
    }

    public static SimpleGraph fromFile(String filePath) {
        return fromFile(filePath, true);
    }

    public void readGraph(String fileName) {
        BufferedReader bufferedReader = null;
        int maxIndegree = 0;
        try {
            InputStream in = new FileInputStream(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine;

            // The first line must have format - "<n> <m>"
            sCurrentLine = bufferedReader.readLine();
            String[] firstLine = sCurrentLine.split("\\s");
            int numberOfVertices = Integer.parseInt(firstLine[0]);
            int numberOfEdges = Integer.parseInt(firstLine[1]);
            initializeGraphStructure(numberOfVertices, numberOfEdges);

            //Read the edges line by line
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\\s", 3);
                int nodeFrom = Integer.parseInt(inputLine[0]);
                int nodeTo = Integer.parseInt(inputLine[1]);

                if (nodeFrom != nodeTo) {
                    addEdge(this.graph, nodeFrom, nodeTo);

                    this.inDegree[nodeTo] = this.inDegree[nodeTo] + 1;
                    if(this.inDegree[nodeTo] > maxIndegree) maxIndegree = this.inDegree[nodeTo];
                    addEdge(this.graphTranspose, nodeTo, nodeFrom);
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEdge(int[][] graph, int u, int v) {
        int[] neighboursU = graph[u];
        if(neighboursU==null) {
            neighboursU = new int[0];
            graph[u] = neighboursU;
        }
        int[] neighboursV = graph[v];
        if(neighboursV==null) {
            neighboursV = new int[0];
            graph[v] = neighboursV;
        }
        int[] newNeighbours = Arrays.copyOf(neighboursU, neighboursU.length+1);
        newNeighbours[neighboursU.length] = v;
        graph[u] = newNeighbours;

    }

    private void initializeGraphStructure(int n, int m) {
        this.graph = new int[n][];
        this.graphTranspose = new int[n][];
        this.inDegree = new int[n];
        this.n = n;
        this.m = m;
    }
}
