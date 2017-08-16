package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naresh on 3/1/2016.
 */
public class FileDataReader implements DataReader {

    String filename;
    private float probability;

    public FileDataReader(String filename, float probability) {
        this.filename = filename;
        this.probability = probability;
    }

    @Override
    public DirectedGraph createGraphFromData() {
        DirectedGraph graph = new DirectedGraph();
        BufferedReader bufferedReader = null;
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("data/" + this.filename);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine;
            int m = 0;
            bufferedReader.readLine();
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\\s", 3);
                int nodeFrom = Integer.parseInt(inputLine[0]);
                int nodeTo = Integer.parseInt(inputLine[1]);
                if (nodeFrom != nodeTo) {
                    graph.addEdge(nodeFrom, nodeTo, this.probability);
                }
                m++;
//                if(m%10000000==0) {
//                    System.out.println("Added edge: " + m);
//                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public Object[] readSimpleGraph() {
        String fileName = this.filename;
        Object[] graph = null;
        BufferedReader bufferedReader = null;
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("data/" + this.filename);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine;
            boolean first = true;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\\s", 3);

                int nodeFrom = Integer.parseInt(inputLine[0]);
                int nodeTo = Integer.parseInt(inputLine[1]);

                if (first) {
                    //The first line should contain n and m
                    first = false;
                    graph = new Object[nodeFrom];
                    continue;
                }

                if (nodeFrom != nodeTo) {
                    addEdge(graph, nodeFrom, nodeTo);
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }

    private void addEdge(Object[] graph, int u, int v) {
        List<Integer> neighboursU = (List<Integer>)graph[u];
        if(neighboursU==null) {
            neighboursU = new ArrayList<>();
            graph[u] = neighboursU;
        }
        List<Integer> neighboursV = (List<Integer>)graph[v];
        if(neighboursV==null) {
            neighboursV = new ArrayList<>();
            graph[v] = neighboursV;
        }
        neighboursU.add(v);

    }
}