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
                RandomRRSet randromRRSet = this.randomRRSetGenerator.generateRandomRRSetWithLabel(this.targetLabel);

                //Calculate K(R)
                double a = 1 - Double.valueOf(randromRRSet.width)/Double.valueOf(m);
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

    private Set<Vertex> nodeSelection(DirectedGraph graph, double epsilon, double opt, int k) {
        int n = graph.getNumberOfVertices();
        int m = graph.getNoOfEdges();
        double R = (8+2 * epsilon) * (Math.log(n) + Math.log(2) + n * logcnk(n,k))/(epsilon * epsilon * opt);
        RandomRRSetGenerator randomRRSetGenerator = new RandomRRSetGenerator(graph);
        HashMap<Vertex, Integer> vertexCount = new HashMap<>();
        for (int i = 0; i < R; i++) {
            RandomRRSet randomRRSet = randomRRSetGenerator.generateRandomRRSet();
            for (Vertex u :
                    randomRRSet.randomRRSet) {
                int count = 0;
                if(vertexCount.get(u)!=null) {
                    count = vertexCount.get(u);
                }
                count++;
                vertexCount.put(u, count);
            }
        }

        Set<Vertex> seedSet = new HashSet<>();
        for (int i = 0; i < k; i++) {
            seedSet.add(getMaximumVertex(vertexCount));
        }

        return seedSet;
    }

    private Vertex getMaximumVertex(HashMap<Vertex, Integer> vertexMap) {
        int maxCount = Integer.MIN_VALUE;
        Vertex maxVertex = null;
        for (Vertex v:
             vertexMap.keySet()) {
            int c = vertexMap.get(v);
            if(c>maxCount) {
                maxVertex = v;
                maxCount = c;
            }

        }
        vertexMap.remove(maxVertex);
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
        MaxTargetInfluentialNodeWithTIM maxTargetInfluentialNode = new MaxTargetInfluentialNodeWithTIM();
        maxTargetInfluentialNode.randomRRSetGenerator = new RandomRRSetGenerator(graph);
        maxTargetInfluentialNode.targetLabel = null;
        int k = 20;
        double epsilon = 0.1;
        double kpt = maxTargetInfluentialNode.estimateKPT(graph, k);
        Set<Vertex> seedSet = maxTargetInfluentialNode.nodeSelection(graph, epsilon, kpt, k);
        Set<Integer> seedSetInteger = new HashSet<>();
        for (Vertex v:
             seedSet) {
            seedSetInteger.add(v.getId());
            System.out.println("Seed set " + v.getId());
        }

        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSetInteger, 10000, new HashSet<>());
        System.out.println("Activated set size : "+ activatedSet.size());
    }

}
