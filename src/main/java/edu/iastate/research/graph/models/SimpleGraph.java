package edu.iastate.research.graph.models;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleGraph {
    private int[][] graph;
    private int[][] graphTranspose;
    private int n;
    private int m;
    private int inDegree[];
    private int[][] edgeNumbers;
    private int maxIndegree = 0;
    private boolean[] labels;

    public static SimpleGraph fromFile(String filePath, boolean buildTranspose) {
        SimpleGraph graph = new SimpleGraph();
        graph.readGraph(filePath);
        return graph;
    }

    public static SimpleGraph fromFileWithLabels(String filePath, String labelsPath) {
        SimpleGraph graph = fromFile(filePath);
        graph.readLabels(labelsPath);
        return graph;
    }

    public static SimpleGraph fromFileWithLabels(String filePath, float targetPercentage) {
        String labelFilePath = String.format("%s_%.1f_labels.txt", filePath, targetPercentage);
        return fromFileWithLabels(filePath, labelFilePath);
    }

    public void readLabels(String labelsPath) {
        this.labels = new boolean[this.n];
        BufferedReader bufferedReader = null;
        try {
            InputStream in = new FileInputStream(labelsPath);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine;

            //Read the labels
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\\s", 2);
                int node = Integer.parseInt(inputLine[0]);
                String label = inputLine[1];
                if(label.compareToIgnoreCase("A")==0) {
                    this.labels[node] = true;
                }
                else {
                    this.labels[node] = false;
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getLabel(int node) {
        return this.labels[node];
    }

    public void setLabel(int node, boolean label) {
        this.labels[node] = label;
    }

    public int countNodesWithLabel(Set<Integer> vertices, boolean label) {
        int count = 0;
        for (int vertex :
                vertices) {
            if(label==this.labels[vertex]) count++;
        }
        return count;
    }

    public int countTargets(Set<Integer> vertices) {
        return countNodesWithLabel(vertices, true);
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
                    processEdge(nodeFrom, nodeTo);
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processEdge(int nodeFrom, int nodeTo) {
        addEdge(this.graph, nodeFrom, nodeTo);

        this.inDegree[nodeTo] = this.inDegree[nodeTo] + 1;
        if(this.inDegree[nodeTo] > this.maxIndegree) this.maxIndegree = this.inDegree[nodeTo];
        addEdge(this.graphTranspose, nodeTo, nodeFrom);
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

    public void initializeGraphStructure(int n, int m) {
        this.graph = new int[n][];
        this.graphTranspose = new int[n][];
        this.inDegree = new int[n];
        this.n = n;
        this.m = m;
        this.labels = new boolean[n];
    }

    public void generateEdgeNumbers() {
        int[][] edgeNumbers = new int[m][2];
        int edgeCounter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < this.graph[i].length; j++) {
                edgeNumbers[edgeCounter][0] = i;
                edgeNumbers[edgeCounter][1] = j;
                edgeCounter++;
            }
        }
    }

    public int[] createDag() {
        List<Integer> activeEdges = new ArrayList<>();
        int edgeCount = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < this.graph[i].length; j++) {

                float p = ThreadLocalRandom.current().nextFloat();
                float propogationProbability = Float.valueOf(1) / Float.valueOf(this.inDegree[j]);
                if (p>propogationProbability) {
                    activeEdges.add(edgeCount);
                }
                edgeCount++;
            }
        }
        int[] activeEdgesArray = new int[activeEdges.size()];
        for (int i = 0; i < activeEdges.size(); i++) {
            activeEdgesArray[i] = activeEdges.get(i);
        }
        return activeEdgesArray;

    }


}
