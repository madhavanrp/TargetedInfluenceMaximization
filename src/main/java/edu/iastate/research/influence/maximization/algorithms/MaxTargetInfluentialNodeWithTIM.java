package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class MaxTargetInfluentialNodeWithTIM extends MaxTargetInfluentialNode {

    RandomRRSetGenerator randomRRSetGenerator;
    String targetLabel = null;
    TIMRandomRRSetMap timRandomRRSetMap;
    int[][] randomRRSetArray;
    private SimpleGraph graph;

    public MaxTargetInfluentialNodeWithTIM() {

    }
    public MaxTargetInfluentialNodeWithTIM(TIMRandomRRSetMap timRandomRRSetMap) {
        this.timRandomRRSetMap = timRandomRRSetMap;
    }

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return new ArrayList<>();
    }


    protected double estimateKPT(int[][] graph, int m, int k) {
        int n = graph.length;
        //log_2(n) - #TODO: Does this need more accurate computation?
        double y = logBase2(n);
        int l = 1;
        double z = (6 * l * Math.log(n) + 6 * Math.log(logBase2(n)));
        for (int i = 1; i <= y-1; i++) {
            double c = z * Math.pow(2, i);
            double sum = 0;
            for (int j = 1; j <=c ; j++) {
                int[][] randomRRSet = this.randomRRSetGenerator.generateRandomRRSetWithLabel();
                int width = randomRRSet[1][0];
                //Calculate K(R)
                double a = 1 - (double)width/(double)m;
                double k_r = 1 - Math.pow(a, k);
                sum+=k_r;
            }

            if((sum/c)>(1f/Math.pow(2, i))) {
                double KPT = n * sum/(2 * c);
                return KPT;
            }

        }
        return 1;
        
    }

    private Set<Integer> nodeSelection(SimpleGraph graph, int[] inDegree, double epsilon, double opt, int k) {
        int n = graph.getNumberOfVertices();
        double R = (8+2 * epsilon) * (Math.log(n) + Math.log(2) + n * logcnk(n,k))/(epsilon * epsilon * opt);
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        System.out.println("R value is " + R);
        int maxSize = 0;
        int[] some = new int[(int)Math.ceil(R)];
        int[][] randomRRSetArray = new int[(int)Math.ceil(R)][];
        this.randomRRSetArray = randomRRSetArray;
        long startTime, endTime;
        long totalTime = 0;

        long incrementStartTime, incrementEndTime;
        long totalIncrementTime = 0;
        for (int i = 0; i < R; i++) {
            startTime = System.currentTimeMillis();
            int[][] randomRRSet = randomRRSetGenerator.generateRandomRRSetWithLabel();
            endTime = System.currentTimeMillis();
            totalTime = totalTime + endTime - startTime;
            randomRRSetArray[i] =randomRRSet[0];
            int j = 0;
            if(randomRRSet[0].length>maxSize) maxSize = randomRRSet[0].length;

            if(i%1000000==0 && i>0) {
                System.out.println("Generated RR Set" + i);
                System.out.println("Max Size so far is " + maxSize);
                System.out.println("Average time to initialise random RR set : " + (double)totalTime/(double) 1000000);
                System.out.println("Total time to initialise random RR set : " + totalTime);
                totalTime = 0;
            }
        }
        startTime = System.currentTimeMillis();
        for (int i = 0; i < R; i++) {
            int[] randomRRSet = randomRRSetArray[i];
            for (int j = 0; j < randomRRSet.length; j++) {
                int u = randomRRSet[j];
                this.timRandomRRSetMap.incrementCountForVertex(u, i);
            }
            if(i%1000000==0) {
                System.out.println("Completed processing for RRsets " + i);
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time taken to increment count for all RR Sets: " + (endTime-startTime));
        System.out.println("Average Time taken to increment count for all RR Sets: " + (endTime-startTime) * 1.0/R);
        System.out.println("RR Sets generated size: " + randomRRSetArray.length);

        return constructSeedSet(graph, k, (int)Math.ceil(R), randomRRSetArray);
    }

    Set<Integer> constructSeedSet(SimpleGraph graph, int k, int R, int[][] randomRRSetArray) {
        PriorityQueue<int[]> queue = new PriorityQueue<>((o1,o2) -> o2[1] - o1[1]);
        int n = graph.getNumberOfVertices();
        Set<Integer> seedSet = new HashSet<>();
        int[] coverage = new int[n];
        boolean[] edgeMark = new boolean[R];
        boolean[] nodeMark = new boolean[n];
        for (int i = 0; i < n; i++) {

            int numberCovered = this.timRandomRRSetMap.countForVertex(i);
            queue.add(new int[]{i, numberCovered});
            coverage[i] = numberCovered;
            nodeMark[i] = true;
        }
        while(seedSet.size()<k) {
            int[] element = queue.peek();
            if(element[1] > coverage[element[0]]) {
                element[1] = coverage[element[0]];
                continue;
            }
            System.out.println("Starting " + seedSet.size());

            element = queue.poll();
            seedSet.add(element[0]);
            nodeMark[element[0]] = false;

            int numberCovered = this.timRandomRRSetMap.countForVertex(element[0]);
            List<Integer> edgeInfluence = this.timRandomRRSetMap.get(element[0]);
            for (int i = 0; i < numberCovered; i++) {
                if(edgeMark[edgeInfluence.get(i)]) continue;

                int[] nList = randomRRSetArray[i];
                for (int l :
                        nList) {
                    if (nodeMark[l]) {
                        coverage[l]--;
                    }
                }
                edgeMark[edgeInfluence.get(i)] = true;
            }
            System.out.println("Finishing " + seedSet.size());
        }

        return seedSet;
    }

    private Integer getMaximumVertex(TIMRandomRRSetMap timRandomRRSetMap) {
        int maxCount = Integer.MIN_VALUE;
        Integer maxVertex = null;
        for (int v = 0; v< this.graph.getNumberOfVertices();v++) {
            int c = timRandomRRSetMap.countForVertex(v);
            if(c>maxCount) {
                maxVertex = v;
                maxCount = c;
            }

        }

        timRandomRRSetMap.removeVertexEntry(maxVertex, this.randomRRSetArray);
        return maxVertex;
    }

    private static double logcnk(int n, int k){
        double ans=0;
        for(int i=n-k+1; i<=n; i++){
            ans+=Math.log(i);
        }
        for(int i=1; i<=k; i++){
            ans-=Math.log(i);
        }
        return ans;
    }

    static double logBase2(int n) {
        return Math.log(n)/Math.log(2);
    }

    public static void main(String[] args) {
        String graphName = "graph_ic.inf";
        URL url = MaxTargetInfluentialNodeWithTIM.class.getClassLoader().getResource("data" + File.separator + graphName);
        SimpleGraph graph = SimpleGraph.fromFile(url.getPath());
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNode = new MaxTargetInfluentialNodeWithTIM();
        maxTargetInfluentialNode.timRandomRRSetMap = new TIMRandomRRSetMap(graph);
        int[] inDegree = graph.getInDegree();
        int m = graph.getNumberOfEdges();
        maxTargetInfluentialNode.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        maxTargetInfluentialNode.targetLabel = null;
        maxTargetInfluentialNode.graph = graph;
        int k = 10;
        double epsilon = 0.1;
        double kpt = maxTargetInfluentialNode.estimateKPT(graph.getGraph(), m, k);
        System.out.println("KPT estimate is " + kpt);
        Set<Integer> seedSet = maxTargetInfluentialNode.nodeSelection(graph, inDegree, epsilon, kpt, k);

        System.out.println("Seed set: " + seedSet);
        DirectedGraph directedGraph = new FileDataReader(graphName, 0.05f).createGraphFromData();
        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(directedGraph, seedSet, 10000, new HashSet<>());
        System.out.println("Activated set size : "+ activatedSet.size());


    }


}
