package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.io.*;
import java.util.*;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class MaxTargetInfluentialNodeWithTIM extends MaxTargetInfluentialNode {

    RandomRRSetGenerator randomRRSetGenerator;
    String targetLabel = null;
    TIMRandomRRSetMap timRandomRRSetMap;
    int[][] randomRRSetArray;

    public MaxTargetInfluentialNodeWithTIM() {

    }
    public MaxTargetInfluentialNodeWithTIM(TIMRandomRRSetMap timRandomRRSetMap) {
        this.timRandomRRSetMap = timRandomRRSetMap;
    }

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return new ArrayList<>();
    }


    protected double estimateKPT(Object[] graph, int m, int k) {
        int n = graph.length;
        //log_2(n) - #TODO: Does this need more accurate computation?
        double y = logBase2(n);
        int l = 1;
        double z = (6 * l * Math.log(n) + 6 * Math.log(logBase2(n)));
        for (int i = 1; i <= y-1; i++) {
            double c = z * Math.pow(2, i);
            double sum = 0;
            for (int j = 1; j <=c ; j++) {
                int[][] randomRRSet = this.randomRRSetGenerator.generateRandomRRSetWithLabel(this.targetLabel);
                int width = randomRRSet[2][0];
                //Calculate K(R)
                double a = 1 - Double.valueOf(width)/Double.valueOf(m);
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

    private Set<Integer> nodeSelection(Object[] graph, int[] inDegree, double epsilon, double opt, int k) {
        int n = graph.length;
        double R = (8+2 * epsilon) * (Math.log(n) + Math.log(2) + n * logcnk(n,k))/(epsilon * epsilon * opt);
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph, inDegree);
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
            int[][] randomRRSet = randomRRSetGenerator.generateRandomRRSet();
            endTime = System.currentTimeMillis();
            totalTime = totalTime + endTime - startTime;
            incrementStartTime = System.currentTimeMillis();
            for (int j = 0; j < randomRRSet[1].length; j++) {
                int u = randomRRSet[1][j];
                this.timRandomRRSetMap.incrementCountForVertex(u, i);
            }
            incrementEndTime = System.currentTimeMillis();
            totalIncrementTime = totalIncrementTime + (incrementEndTime - incrementStartTime);
            randomRRSetArray[i] =randomRRSet[1];
            int j = 0;
            if(randomRRSet[1].length>maxSize) maxSize = randomRRSet[1].length;

            if(i%1000000==0 && i>0) {
                System.out.println("Generated RR Set" + i);
                System.out.println("Max Size so far is " + maxSize);
                System.out.println("Average time to initialise random RR set : " + (double)totalTime/(double) 1000000);
                System.out.println("Average time to increment vertex count is  : " + (double)totalIncrementTime/(double) 1000000);
                System.out.println("Total time to initialise random RR set : " + totalTime);
                System.out.println("Total time to increment vertex count is  : " + totalIncrementTime);
                totalTime = 0;
                totalIncrementTime = 0;
            }
        }
        System.out.println("RR Sets generated size: " + randomRRSetArray.length);

        Set<Integer> seedSet = new HashSet<>();
        for (int i = 0; i < k; i++) {
            System.out.println("Starting " + i);
            seedSet.add(getMaximumVertex(this.timRandomRRSetMap));
            System.out.println("Finishing " + i);
        }

        return seedSet;
    }

    private Integer getMaximumVertex(TIMRandomRRSetMap timRandomRRSetMap) {
        int maxCount = Integer.MIN_VALUE;
        Integer maxVertex = null;
        System.out.println("Starting to take max vertex");
        for (int v = 0; v< this.graph.length;v++) {
            int c = timRandomRRSetMap.countForVertex(v);
            if(c>maxCount) {
                maxVertex = v;
                maxCount = c;
            }

        }
        System.out.println("Starting to take max vertex");

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
        Object[] graph = new FileDataReader("graph_ic.inf", 0.01f).readSimpleGraph();
        System.out.println("Read graph " + graph.length);
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNode = new MaxTargetInfluentialNodeWithTIM();
        maxTargetInfluentialNode.timRandomRRSetMap = new TIMRandomRRSetMap(graph);
        int[] inDegree = maxTargetInfluentialNode.inDegree(graph);
        int m = maxTargetInfluentialNode.edges(graph);
        maxTargetInfluentialNode.randomRRSetGenerator = new RandomRRSetGenerator(graph, inDegree);
        maxTargetInfluentialNode.targetLabel = null;
        maxTargetInfluentialNode.graph = graph;
        int k = 40;
        double epsilon = 0.1;
        double kpt = maxTargetInfluentialNode.estimateKPT(graph, m, k);
        System.out.println("KPT estimate is " + kpt);
        Set<Integer> seedSet = maxTargetInfluentialNode.nodeSelection(graph, inDegree, epsilon, kpt, k);

        System.out.println("Seed set: " + seedSet);
//        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, 10000, new HashSet<>());
//        System.out.println("Activated set size : "+ activatedSet.size());


    }

    private int edges(Object[] graph) {
        int m = 0;
        for (int i = 0; i < graph.length; i++) {
            List<Integer> outboundVertices = (List<Integer>) graph[i];
            for (Integer v :
                    outboundVertices) {
                m++;
            }
        }
        return m;
    }

    private int[] inDegree(Object[] graph) {
        int[] inDegree = new int[graph.length];
        for (int i = 0; i < graph.length; i++) {
            List<Integer> outboundVertices = (List<Integer>) graph[i];
            for (Integer v :
                    outboundVertices) {
                inDegree[v] = inDegree[v] + 1;
            }
        }
        return inDegree;
    }

    private Object[] graph;

}
