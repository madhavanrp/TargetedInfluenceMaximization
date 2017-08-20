package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.models.VertexWithFlag;
import edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNodeWithTIM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Naresh on 1/17/2017.
 */
public class GenerateGraphLabels {

    public void generateLabels(String filename, float aPercentage) {
        FileDataReader wikiVoteDataReader = new FileDataReader(filename, 0.05f);
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        Path currentRelativePath = Paths.get("");
        String baseProjectPath = currentRelativePath.toAbsolutePath().toString();
        List<String> pathArrayList = Arrays.asList(baseProjectPath, "src", "main", "resources", "data");
        String outputPath = String.join(File.separator, pathArrayList);
        File output = new File(outputPath + File.separator + filename + "_" + aPercentage + "_labels.txt");
        System.out.println("Writing labels to  "+ output.getAbsolutePath());
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
        generateGraphLabels.generateLabels("wiki-Vote.txt",0.8f);
//        generateGraphLabels.generateLabels("dblp-tang.txt",0.2f);
    }

}
