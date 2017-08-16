package edu.iastate.research.influence.maximization.algorithms;


import edu.iastate.research.graph.models.SimpleGraph;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;


/**
 * Created by madhavanrp on 7/29/17.
 */
public class TIMRandomRRSetMap {

    //This has the following structure: {<vertex>: [count, List of random RR sets in which it appears]}
    private static List<Integer>[] lookupTable = null;
    private boolean[] nodeMark;
    private boolean[] edgeMark;
    private int[] coverage;
    public boolean[] getNodeMark() {
        return nodeMark;
    }

    public void setNodeMark(boolean[] nodeMark) {
        this.nodeMark = nodeMark;
    }

    public boolean[] getEdgeMark() {
        return edgeMark;
    }

    public void setEdgeMark(boolean[] edgeMark) {
        this.edgeMark = edgeMark;
    }

    public PriorityQueue<int[]> getQueue() {
        return queue;
    }

    public void setQueue(PriorityQueue<int[]> queue) {
        this.queue = queue;
    }

    private PriorityQueue<int[]> queue;

    public TIMRandomRRSetMap(SimpleGraph graph) {
        if(lookupTable == null) {
            lookupTable = new ArrayList[graph.getNumberOfVertices()];
        }

    }

    public static void clearLookupTable() {
        lookupTable = null;
    }

    public List<Integer> get(int vertex) {
        return lookupTable[vertex];
    }

    public void removeVertexEntry(Integer u, int[][] randomRRSetArray) {
        List<Integer>  setsCoveredByVertex = this.lookupTable[u];
        if(setsCoveredByVertex==null) return;
        for (int setID :
                setsCoveredByVertex) {
            int[] verticesInSet = randomRRSetArray[setID];
            for (int i = 0; i < verticesInSet.length; i++) {
                int vertex = verticesInSet[i];
                if(vertex==u) continue;
                decrementCountForVertex(vertex, setID);
            }
        }

        this.lookupTable[u] = null;
    }

    public void decrementCountForVertex(int u, int setID) {
        List<Integer> entry = this.lookupTable[u];
        if(entry==null) return;
        int count = entry.size();
//        entry = ArrayUtils.removeElement(entry, setID);
        entry.remove((Integer)setID);
        this.lookupTable[u] = entry;
    }

    public void incrementCountForVertex(Integer vertex, int randomRRSetID) {
        List<Integer> rrSetsCovered = this.lookupTable[vertex];
        int oldLength = 0;
        if(rrSetsCovered==null) {
            rrSetsCovered = new ArrayList<>();
        }
//        else {
//            oldLength = rrSetsCovered.length;
//            rrSetsCovered = Arrays.copyOf(rrSetsCovered, oldLength+1);
//        }
//        rrSetsCovered[oldLength] = randomRRSetID;
        rrSetsCovered.add(randomRRSetID);
        this.lookupTable[vertex] = rrSetsCovered;
    }

    public int countForVertex(Integer u) {
        List<Integer> rrSetsCovered = this.lookupTable[u];
        if(rrSetsCovered==null) return 0;
        return rrSetsCovered.size();
    }

    public int[] getCoverage() {
        return coverage;
    }

    public void setCoverage(int[] coverage) {
        this.coverage = coverage;
    }

    public TIMRandomRRSetMap createCopy() {
        TIMRandomRRSetMap copyTimRandomRRSetMap = new TIMRandomRRSetMap(null);
        int[] coverage = this.getCoverage();
        boolean[] edgeMark = this.getEdgeMark();
        boolean[] nodeMark = this.getNodeMark();
        int[] coverageCopy = Arrays.copyOf(coverage, coverage.length);
        boolean[] edgeMarkCopy = Arrays.copyOf(edgeMark, edgeMark.length);
        boolean[] nodeMarkCopy = Arrays.copyOf(nodeMark, nodeMark.length);

        PriorityQueue<int[]> queue = this.getQueue();
        PriorityQueue<int[]> queueCopy = new PriorityQueue<>(queue.comparator());
        for (int[] elements :
                queue) {
            queueCopy.add(Arrays.copyOf(elements, elements.length));
        }

        copyTimRandomRRSetMap.setQueue(queueCopy);
        copyTimRandomRRSetMap.setEdgeMark(edgeMarkCopy);
        copyTimRandomRRSetMap.setNodeMark(nodeMarkCopy);
        copyTimRandomRRSetMap.setCoverage(coverageCopy);
        return copyTimRandomRRSetMap;
    }
}
