package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.SimpleGraph;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class MaxTargetInfluentialNodeWithTIM extends MaxTargetInfluentialNode {
    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeWithTIM.class);

    RandomRRSetGenerator randomRRSetGenerator;
    String targetLabel = null;

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }

    private double R;

    public TIMRandomRRSetMap getTimRandomRRSetMap() {
        return timRandomRRSetMap;
    }

    public void setTimRandomRRSetMap(TIMRandomRRSetMap timRandomRRSetMap) {
        this.timRandomRRSetMap = timRandomRRSetMap;
    }

    private TIMRandomRRSetMap timRandomRRSetMap;

    public RandomRRSetGenerator getRandomRRSetGenerator() {
        return randomRRSetGenerator;
    }

    public void setRandomRRSetGenerator(RandomRRSetGenerator randomRRSetGenerator) {
        this.randomRRSetGenerator = randomRRSetGenerator;
    }

    public int[][] getRandomRRSetArray() {
        return randomRRSetArray;
    }

    public void setRandomRRSetArray(int[][] randomRRSetArray) {
        this.randomRRSetArray = randomRRSetArray;
    }

    int[][] randomRRSetArray;
    private SimpleGraph graph;
    private int[] nonTargetMap;

    public MaxTargetInfluentialNodeWithTIM() {

    }
    public MaxTargetInfluentialNodeWithTIM(TIMRandomRRSetMap timRandomRRSetMap) {
        this.timRandomRRSetMap = timRandomRRSetMap;
    }

    @Override
    public List<NodeWithInfluence> find(Object graphObject, Set<Integer> candidateNodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        Set<Integer> candidateNodesWithoutSeedSet = new HashSet<>(candidateNodes);
        candidateNodesWithoutSeedSet.removeAll(seedSet);
        NodeWithInfluence nodeWithInfluence = findNodeWithMaximumMarginalGain(candidateNodesWithoutSeedSet);
        List<NodeWithInfluence> list = new ArrayList<>();
        list.add(nodeWithInfluence);
        return list;
    }


    protected double estimateKPT(int[][] graph, int m, int k) {
        int n = graph.length;
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

    public static double estimateKPT(RandomRRSetGenerator randomRRSetGenerator, int n, int m, int k) {
        double y = logBase2(n);
        int l = 1;
        double z = (6 * l * Math.log(n) + 6 * Math.log(logBase2(n)));
        for (int i = 1; i <= y-1; i++) {
            double c = z * Math.pow(2, i);
            double sum = 0;
            for (int j = 1; j <=c ; j++) {
                int[][] randomRRSet = randomRRSetGenerator.generateRandomRRSetWithLabel();
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

    public static int[][] generateRandomRRsetArray(SimpleGraph graph, double R) {
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        logger.info("R value is " + R);
        int maxSize = 0;
        int[][] randomRRSetArray = new int[(int)Math.ceil(R)][];
        long startTime, endTime;
        long totalTime = 0;

        for (int i = 0; i < R; i++) {
            startTime = System.currentTimeMillis();
            int[][] randomRRSet = randomRRSetGenerator.generateRandomRRSetWithLabel();
            endTime = System.currentTimeMillis();
            totalTime = totalTime + endTime - startTime;
            randomRRSetArray[i] =randomRRSet[0];
            int j = 0;
            if(randomRRSet[0].length>maxSize) maxSize = randomRRSet[0].length;

//            if(i%1000000==0 && i>0) {
//                System.out.println("Generated RR Set" + i);
//                System.out.println("Max Size so far is " + maxSize);
//                System.out.println("Average time to initialise random RR set : " + (double)totalTime/(double) 1000000);
//                System.out.println("Total time to initialise random RR set : " + totalTime);
//                totalTime = 0;
//            }
        }

        return randomRRSetArray;
    }

    public static double calculateRValue(int n, double epsilon, double opt, int k) {
        double R = (8+2 * epsilon) * (Math.log(n) + Math.log(2) + n * logcnk(n,k))/(epsilon * epsilon * opt);
        return R;
    }

    public static void incrementVertexCount(TIMRandomRRSetMap timRandomRRSetMap, int[][] randomRRSetArray, double R) {
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < R; i++) {
            int[] randomRRSet = randomRRSetArray[i];
            for (int j = 0; j < randomRRSet.length; j++) {
                int u = randomRRSet[j];
                timRandomRRSetMap.incrementCountForVertex(u, i);
            }
//            if(i%1000000==0) {
//                System.out.println("Completed processing for RRsets " + i);
//            }
        }
        endTime = System.currentTimeMillis();
//        System.out.println("Time taken to increment count for all RR Sets: " + (endTime-startTime));
//        System.out.println("Average Time taken to increment count for all RR Sets: " + (endTime-startTime) * 1.0/R);
//        System.out.println("RR Sets generated size: " + randomRRSetArray.length);
    }

    private Set<Integer> nodeSelection(SimpleGraph graph, int[] inDegree, double epsilon, double opt, int k) {
        int n = graph.getNumberOfVertices();
        double R = calculateRValue(n, epsilon, opt, k);
        int[][] randomRRSetArray = generateRandomRRsetArray(graph, R);
        incrementVertexCount(this.timRandomRRSetMap, randomRRSetArray, R);
        initializeDataStructuresForTIM((int)Math.ceil(R));
        return constructSeedSet(graph, k, (int)Math.ceil(R), randomRRSetArray);
    }

    public void initializeDataStructuresForTIM(int R) {
        PriorityQueue<int[]> queue = new PriorityQueue<>((o1,o2) -> o2[1] - o1[1]);
        int n = graph.getNumberOfVertices();
        int[] coverage = new int[n];
        boolean[] edgeMark = new boolean[R];
        boolean[] nodeMark = new boolean[n];
        for (int i = 0; i < n; i++) {

            int numberCovered = this.timRandomRRSetMap.countForVertex(i);
            queue.add(new int[]{i, numberCovered});
            coverage[i] = numberCovered;
            nodeMark[i] = true;
        }

        this.timRandomRRSetMap.setEdgeMark(edgeMark);
        this.timRandomRRSetMap.setNodeMark(nodeMark);
        this.timRandomRRSetMap.setQueue(queue);
        this.timRandomRRSetMap.setCoverage(coverage);
    }

    public MaxTargetInfluentialNodeWithTIM  createCopy() {
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNodeWithTIMCopy = new MaxTargetInfluentialNodeWithTIM();
        TIMRandomRRSetMap timRandomRRSetMapCopy = this.timRandomRRSetMap.createCopy();
        maxTargetInfluentialNodeWithTIMCopy.setTimRandomRRSetMap(timRandomRRSetMapCopy);
        maxTargetInfluentialNodeWithTIMCopy.setRandomRRSetArray(this.randomRRSetArray);
        maxTargetInfluentialNodeWithTIMCopy.setR(getR());
        maxTargetInfluentialNodeWithTIMCopy.setGraph(this.graph);
        return maxTargetInfluentialNodeWithTIMCopy;
    }

    Set<Integer> constructSeedSet(SimpleGraph graph, int k, int R, int[][] randomRRSetArray) {
        Set<Integer> seedSet = new HashSet<>();
        PriorityQueue<int[]> queue = this.timRandomRRSetMap.getQueue();
        int[] coverage = this.timRandomRRSetMap.getCoverage();
        boolean[] nodeMark = this.timRandomRRSetMap.getNodeMark();
        boolean[] edgeMark = this.timRandomRRSetMap.getEdgeMark();

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

                int[] nList = randomRRSetArray[edgeInfluence.get(i)];
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
        String graphName = "dblp-tang.txt";
        URL url = MaxTargetInfluentialNodeWithTIM.class.getClassLoader().getResource("data" + File.separator + graphName);
        SimpleGraph graph = SimpleGraph.fromFileWithLabels(url.getPath(), 0.8f);
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
        System.out.println("Activated targets size: " + graph.countTargets(activatedSet));


    }
    public NodeWithInfluence findNodeWithMaximumMarginalGain(Set<Integer> candidateNodes ) {
        PriorityQueue<int[]> queue = this.timRandomRRSetMap.getQueue();
        PriorityQueue<int[]> queueCopy = new PriorityQueue<>(queue);
        int[] coverage = this.timRandomRRSetMap.getCoverage();
        boolean[] nodeMark = this.timRandomRRSetMap.getNodeMark();
        int maximumGainNode = Integer.MIN_VALUE;
        int influence = 0;
        while(!queue.isEmpty()) {
            int[] element = queue.peek();
            if(element[1] > coverage[element[0]]) {
                element[1] = coverage[element[0]];
                continue;
            }

            //Make sure the correct node is removed
            element = queue.poll();
            if(!nodeMark[element[0]]) {
                queue.remove(element);
                continue;
            }
            if(candidateNodes.contains(element[0])) {
                maximumGainNode = element[0];
                influence = coverage[element[0]];
                break;
            }

        }
        this.timRandomRRSetMap.setQueue(queueCopy);
        NodeWithInfluence nodeWithInfluence = new NodeWithInfluence(maximumGainNode, influence);
        return nodeWithInfluence;
    }
    public void addToSeed(int nodeID, int[][] randomRRSetArray) {
        int[] coverage = this.timRandomRRSetMap.getCoverage();
        boolean[] nodeMark = this.timRandomRRSetMap.getNodeMark();
        boolean[] edgeMark = this.timRandomRRSetMap.getEdgeMark();
        nodeMark[nodeID] = false;
        int numberCovered = this.timRandomRRSetMap.countForVertex(nodeID);
        List<Integer> edgeInfluence = this.timRandomRRSetMap.get(nodeID);
        for (int i = 0; i < numberCovered; i++) {
            if (edgeMark[edgeInfluence.get(i)]) continue;

            int[] nList = randomRRSetArray[edgeInfluence.get(i)];
            for (int l :
                    nList) {
                if (nodeMark[l]) {
                    coverage[l]--;
                }
            }
            edgeMark[edgeInfluence.get(i)] = true;
        }

    }
    public void setGraph(SimpleGraph graph) {
        this.graph = graph;
    }


}
