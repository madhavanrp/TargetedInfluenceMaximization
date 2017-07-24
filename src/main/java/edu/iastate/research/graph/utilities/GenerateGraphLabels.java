package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.models.VertexWithFlag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Naresh on 1/17/2017.
 */
public class GenerateGraphLabels {

    public void generateLabels(String filename, float aPercentage) {
        FileDataReader wikiVoteDataReader = new FileDataReader(filename, 0.05f);
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        int i = 0;
        long start = System.nanoTime();
        for (Vertex v :
                graph.getVertices()) {

            Queue<Vertex> queue = new LinkedList<>();
            queue.add(v);
            Set<Vertex> visited = new HashSet<>();
            int m = 0;
            while (!queue.isEmpty()) {
                Vertex u = queue.remove();
                if(visited.contains(u)) continue;
                visited.add(u);
                for (VertexWithFlag d: u.getOutBoundNeighbours()) {
                    queue.add(d.getVertex());
                    m++;

                }
            }
            System.out.println("Finished BFS " + i++ + " Visited edges " + m) ;
            if(i>20) break;
        }
        long end = System.nanoTime();
        System.out.println("Time taken: " + TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS));
        File output = new File(filename + "_" + aPercentage + "_labels.txt");
        try {
            PrintWriter writer = new PrintWriter(output);
            for (Vertex vertex : graph.getVertices()) {
                vertex.setLabel(new Random().nextFloat() > aPercentage ? "B" : "A");
                writer.write(vertex.getId() + "\t" + vertex.getLabel() +"\n");
            }
            writer.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GenerateGraphLabels generateGraphLabels = new GenerateGraphLabels();
        generateGraphLabels.generateLabels("soc-LiveJournal1.txt",0.2f);
//        generateGraphLabels.generateLabels("dblp-tang.txt",0.2f);
    }

}
