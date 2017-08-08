package edu.iastate.research.influence.maximization.algorithms;


import java.util.*;


/**
 * Created by madhavanrp on 7/29/17.
 */
public class TIMRandomRRSetMap {

    //This has the following structure: {<vertex>: [count, List of random RR sets in which it appears]}
    private Object[] lookupTable;
    public TIMRandomRRSetMap(Object[] graph) {
        this.lookupTable = new Object[graph.length];


    }

    public void removeVertexEntry(Integer u, int[][] randomRRSetArray) {
        List<Object> entry = (List<Object>)this.lookupTable[u];
        if(entry==null) return;
        Collection<Integer>  setsCoveredByVertex = (Collection<Integer>) entry.get(1);
        System.out.println(String.format("Number of sets covered by %d is %d", u, setsCoveredByVertex.size()));
        for (Integer setID :
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
        List<Object> entry = (List<Object>)this.lookupTable[u];
        if(entry==null) return;
        int count = (int)entry.get(0);
        count--;
        entry.set(0, count);
        Collection<Integer> setsCovered = (Collection<Integer>)entry.get(1);
        setsCovered.remove((Integer) setID); //This actually removes the integer Object! Not at integer the index. The typecast ensures that.
    }

    public void incrementCountForVertex(Integer vertex, int randomRRSetID) {
        List<Object> list = (List<Object>)this.lookupTable[vertex];
        if(list==null) {
            list = new ArrayList();
            list.add(0);
            list.add(new HashSet<Integer>());
        }
        int count = (Integer)list.get(0);
        count++;
        list.set(0, count);
        Collection<Integer> sets = (Collection<Integer>)list.get(1);
        sets.add(randomRRSetID);
        this.lookupTable[vertex] = list;
    }

    public int countForVertex(Integer u) {
        List<Object> entry = (List<Object>)this.lookupTable[u];
        return (Integer)entry.get(0);
    }
}
