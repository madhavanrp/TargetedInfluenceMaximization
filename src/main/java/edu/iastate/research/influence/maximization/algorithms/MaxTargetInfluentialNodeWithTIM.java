package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.*;

/**
 * Created by madhavanrp on 7/26/17.
 */
public class MaxTargetInfluentialNodeWithTIM extends MaxTargetInfluentialNode {

    RandomRRSetGenerator randomRRSetGenerator;
    String targetLabel = null;
    TIMRandomRRSetMap timRandomRRSetMap;

    public MaxTargetInfluentialNodeWithTIM() {
        this.timRandomRRSetMap = new TIMRandomRRSetMap();

    }
    public MaxTargetInfluentialNodeWithTIM(TIMRandomRRSetMap timRandomRRSetMap) {
        this.timRandomRRSetMap = timRandomRRSetMap;
    }

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        this.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        for (String label: targetLabels) {
            this.targetLabel = label;
            break;
        }
//        double kpt = estimateKPT(graph, 10);
//        PriorityQueue<Vertex> queue = nodeSelection(graph, 0, kpt, 10);
//        NodeWithInfluence nodeWithInfluence = new NodeWithTotalInfluence(queue.remove().getId(), 10,10, 0);
//        List<NodeWithInfluence> nodeWithInfluences = new ArrayList<>();
//        nodeWithInfluences.add(nodeWithInfluence);
//        System.out.println("Returning");
//        return nodeWithInfluences;
        return new ArrayList<>();
    }


    protected double estimateKPT(DirectedGraph graph, int k) {
        int n = graph.getNumberOfVertices();
        int m = graph.getNoOfEdges();
        //log_2(n) - #TODO: Does this need more accurate computation?
        double y = logBase2(n);
        int l = 1;
        double z = (6 * l * Math.log(n) + 6 * Math.log(logBase2(n)));
        for (int i = 1; i <= y-1; i++) {
            double c = z * Math.pow(2, i);
            double sum = 0;
            for (int j = 1; j <=c ; j++) {
                RandomRRSet randomRRSet = this.randomRRSetGenerator.generateRandomRRSetWithLabel(this.targetLabel);

                //Calculate K(R)
                double a = 1 - Double.valueOf(randomRRSet.width)/Double.valueOf(m);
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

    private Set<Integer> nodeSelection(DirectedGraph graph, double epsilon, double opt, int k) {
        int n = graph.getNumberOfVertices();
        int m = graph.getNoOfEdges();
        double R = (8+2 * epsilon) * (Math.log(n) + Math.log(2) + n * logcnk(n,k))/(epsilon * epsilon * opt);
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        HashMap<Vertex, Integer> vertexCount = new HashMap<>();
        System.out.println("R value is " + R);
        int maxSize = 0;
        int[][] randomRRSetArray = new int[(int)Math.ceil(R)][];
        long startTime, endTime;
        long totalTime = 0;
        for (int i = 0; i < R; i++) {
            startTime = System.currentTimeMillis();
            RandomRRSet randomRRSet = randomRRSetGenerator.generateRandomRRSet();
            endTime = System.currentTimeMillis();
            totalTime = totalTime + endTime - startTime;
            randomRRSet.setId(i);
            for (Integer u :
                    randomRRSet.randomRRSet) {
                this.timRandomRRSetMap.incrementCountForVertex(u, randomRRSet);
            }
            randomRRSetArray[i] = new int[randomRRSet.randomRRSet.size()];
            int j = 0;
            for (Integer setId :
                    randomRRSet.randomRRSet) {
                randomRRSetArray[i][j++] = setId;
            }
            if(randomRRSet.randomRRSet.size()>maxSize) maxSize = randomRRSet.randomRRSet.size();

            if(i%1000000==0 && i>0) {
                System.out.println("Generated RR Set" + i);
                System.out.println("Max Size so far is " + maxSize);
                System.out.println("Average time to initialise random RR set : " + (double)totalTime/(double) (i+1));
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
        for (Integer v:
             timRandomRRSetMap.getVertices()) {
            int c = timRandomRRSetMap.countForVertex(v);
            if(c>maxCount) {
                maxVertex = v;
                maxCount = c;
            }

        }
        timRandomRRSetMap.removeVertexEntry(maxVertex);
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
        DirectedGraph graph = new FileDataReader("graph_ic.inf", 1.0f).createGraphFromData();
        Vertex.setPropagationProbability(0.01f);
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNode = new MaxTargetInfluentialNodeWithTIM();
        maxTargetInfluentialNode.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        maxTargetInfluentialNode.targetLabel = null;
        int k = 40;
        double epsilon = 0.1;
        double kpt = maxTargetInfluentialNode.estimateKPT(graph, k);
        Set<Integer> seedSet = maxTargetInfluentialNode.nodeSelection(graph, epsilon, kpt, k);


        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, 10000, new HashSet<>());
        System.out.println("Activated set size : "+ activatedSet.size());
    }

}
