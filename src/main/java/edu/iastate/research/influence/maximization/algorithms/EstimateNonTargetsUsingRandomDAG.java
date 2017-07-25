package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.models.VertexWithFlag;
import edu.iastate.research.graph.utilities.WriteObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static sun.net.ftp.FtpReplyCode.find;


/**
 * Created by Naresh on 1/16/2017.
 */
public class EstimateNonTargetsUsingRandomDAG extends EstimateNonTargets {
    final static Logger logger = Logger.getLogger(EstimateNonTargetsUsingRandomDAG.class);
    private String nonTargetLabel;
    private DirectedGraph fullGraph;
    @Override
    public Map<Integer, Integer> estimate(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        this.fullGraph = graph;
        Map<Integer, Integer> aggregatedNonTargetsMap = new HashMap<>();
        for (String label :
                nonTargetLabels) {
            this.nonTargetLabel = label;
            break;
        }
        for (int i = 0; i < noOfSimulations; i++) {
            System.out.println("Begin randomize");
            DirectedGraph dag = createDAG(graph);
            System.out.println("Randomized dag: " + i);



            long start = System.nanoTime();
            findNonTargetsInDAGWithPrunedBFS(dag, aggregatedNonTargetsMap);
//            findNonTargetsInDAG(dag, nonTargetLabels, aggregatedNonTargetsMap);
            long end = System.nanoTime();
            System.out.println("Time taken for 1 DAG non targets estimation: " + TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS));
        }
        Map<Integer, Integer> estimatedNonTargetMap = avgResults(aggregatedNonTargetsMap, noOfSimulations);

        String filename = UUID.randomUUID().toString() + "-non-targets-map.data";
        logger.info("Writing Estimated Non Targets Map to file " + filename);
        WriteObject.writeToFile(estimatedNonTargetMap, filename);

        return estimatedNonTargetMap;
    }

    private void findNonTargetsInDAGWithPrunedBFS(DirectedGraph dag, Map<Integer, Integer> aggregatedNonTargetsMap) {
        // Get the ancestors and descendants of the maximum degree vertex in the random dag
        Vertex maxDegreeVertex = dag.getMaxDegreeVertex();
        Set<Vertex> ancestors = dag.findAncestors(maxDegreeVertex);
        Set<Vertex> descendants = dag.findDescendants(maxDegreeVertex);
        HashMap<Vertex, Integer> nonTargetMap = new HashMap<>();
        int i = 0;
        for (Vertex v:
             dag.getVertices()) {
            gain(ancestors, descendants, v, dag, nonTargetMap);
        }
        for (Vertex v:
             this.fullGraph.getVertices()) {
            Vertex dagVertex = dag.find(v.getId());

            //If this vertex is in the random dag and has an influence. Otherwise it is 0 or 1 based on label.
            if(dagVertex!=null) {
                int prevNonTargetCount = 0;
                if (aggregatedNonTargetsMap.containsKey(dagVertex.getId())) {
                    prevNonTargetCount = aggregatedNonTargetsMap.get(dagVertex.getId());
                }
                aggregatedNonTargetsMap.put(dagVertex.getId(), prevNonTargetCount + nonTargetMap.get(dagVertex));
            } else {
                int nonTargetCount = 0;
                if(v.getLabel().compareToIgnoreCase(this.nonTargetLabel)==0) {
                    nonTargetCount = 1;
                }
                int prevNonTargetCount = 0;
                if (aggregatedNonTargetsMap.containsKey(v.getId())) {
                    prevNonTargetCount = aggregatedNonTargetsMap.get(v.getId());
                }
                aggregatedNonTargetsMap.put(v.getId(), nonTargetCount + prevNonTargetCount);
            }
        }
    }

    private int gain(Set<Vertex> ancestors, Set<Vertex> descendants, Vertex vertex, DirectedGraph dag, HashMap<Vertex, Integer> nonTargetMap) {
        //Integer non
        if(nonTargetMap.containsKey(vertex)) return nonTargetMap.get(vertex);
        int influence = 0;
        if(ancestors.contains(vertex)) {
            influence = gain(ancestors, descendants, dag.getMaxDegreeVertex(), dag, nonTargetMap);
        }
        nonTargetMap.put(vertex, influence);

        Queue<Vertex> queue = new LinkedList<>();
        queue.add(vertex);
        Set<Vertex> X = new HashSet<>();
        X.add(vertex);
        while (!queue.isEmpty()) {
            Vertex u = queue.remove();
            if(ancestors.contains(vertex) && descendants.contains(u)) continue;
            if(u.getLabel().compareToIgnoreCase(this.nonTargetLabel)==0) {
                influence = nonTargetMap.get(vertex);
                influence++;
                nonTargetMap.put(vertex, influence);
            }

            for (Vertex v :
                    u.getOutBoundNeighbours()) {
                if (X.contains(v)) continue;
                queue.add(v);
                X.add(v);
            }

        }
        return nonTargetMap.get(vertex);
    }

    private Map<Integer, Integer> avgResults(Map<Integer, Integer> aggregatedNonTargetsMap, int noOfSimulations) {
        Map<Integer, Integer> normalizedNonTargetMap = new HashMap<>();
        for (Integer node : aggregatedNonTargetsMap.keySet()) {
            normalizedNonTargetMap.put(node, Math.round(((float) aggregatedNonTargetsMap.get(node)) / noOfSimulations));
        }
        return normalizedNonTargetMap;
    }

    private Map<Integer, Integer> normalizeResults(List<Map<Integer, Integer>> nonTargetsActivatedMapDAGList, int noOfSimulations) {
        Map<Integer, Integer> estimatedActiveNonTargetMap = new HashMap<>();
        for (Map<Integer, Integer> nonTargetsMapInDAG : nonTargetsActivatedMapDAGList) {
            for (Integer vertex : nonTargetsMapInDAG.keySet()) {
                int aggregateNonTargetsActivated = 0;
                if (estimatedActiveNonTargetMap.containsKey(vertex)) {
                    aggregateNonTargetsActivated = estimatedActiveNonTargetMap.get(vertex);
                }
                aggregateNonTargetsActivated += nonTargetsMapInDAG.get(vertex);
                estimatedActiveNonTargetMap.put(vertex, aggregateNonTargetsActivated);
            }
        }
        for (Integer vertex : estimatedActiveNonTargetMap.keySet()) {
            int avgNontargetsActivated = Math.round((float) estimatedActiveNonTargetMap.get(vertex) / noOfSimulations);
            estimatedActiveNonTargetMap.put(vertex, avgNontargetsActivated);
        }
        return estimatedActiveNonTargetMap;
    }

    private void findNonTargetsInDAG(DirectedGraph dag, Set<String> nonTargetLabels, Map<Integer, Integer> aggregatedNonTargetsMap) {
        Map<Integer, Set<Integer>> reachableCache = new HashMap<>();
        for (Vertex v : dag.getVertices()) {
            Set<Integer> reachableSet = new HashSet<>();
            Queue<Integer> bfsQueue = new LinkedList<>();
            bfsQueue.add(v.getId());
            reachableSet.add(v.getId());
            while (!bfsQueue.isEmpty()) {
                int node = bfsQueue.remove();
                if (reachableCache.containsKey(node)) {
                    reachableSet.addAll(reachableCache.get(node));
                } else {
                    for (Vertex vOut : dag.find(node).getOutBoundNeighbours()) {
                        if (!reachableSet.contains(vOut.getId())) {
                            bfsQueue.add(vOut.getId());
                            reachableSet.add(vOut.getId());
                        }
                    }
                }
            }
            reachableCache.put(v.getId(), reachableSet);
            int prevNonTargetCount = 0;
            if (aggregatedNonTargetsMap.containsKey(v.getId())) {
                prevNonTargetCount = aggregatedNonTargetsMap.get(v.getId());
            }
            aggregatedNonTargetsMap.put(v.getId(), prevNonTargetCount + countNonTargets(reachableSet, dag, nonTargetLabels));
        }
    }


    private DirectedGraph createDAG(DirectedGraph graph) {
        DirectedGraph clonedGraph = new DirectedGraph();
        for (Vertex v : graph.getVertices()) {
            for (Vertex vOut : v.getOutBoundNeighbours()) {
                if (!(new Random().nextFloat() < (1 - v.getPropagationProbability(vOut)))) {
                    clonedGraph.addEdge(v, vOut, v.getPropagationProbability(vOut));
                }
            }
        }
        return clonedGraph;
    }


}
