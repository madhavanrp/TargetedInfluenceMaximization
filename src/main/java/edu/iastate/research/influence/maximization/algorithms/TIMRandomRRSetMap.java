package edu.iastate.research.influence.maximization.algorithms;


import edu.iastate.research.graph.models.SimpleGraph;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;


/**
 * Created by madhavanrp on 7/29/17.
 */
public class TIMRandomRRSetMap {

    //This has the following structure: {<vertex>: [count, List of random RR sets in which it appears]}
    private List<Integer>[] lookupTable;
    public TIMRandomRRSetMap(SimpleGraph graph) {
        this.lookupTable = new ArrayList[graph.getNumberOfVertices()];

    }

    public List<Integer> get(int vertex) {
        return this.lookupTable[vertex];
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
}
