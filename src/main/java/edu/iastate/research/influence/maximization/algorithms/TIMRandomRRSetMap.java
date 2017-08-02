package edu.iastate.research.influence.maximization.algorithms;


import java.util.*;


/**
 * Created by madhavanrp on 7/29/17.
 */
public class TIMRandomRRSetMap {

    //This has the following structure: {<vertex>: [count, List of random RR sets in which it appears]}
    private HashMap<Integer, List<Object>> lookupTable;
    public TIMRandomRRSetMap() {
        this.lookupTable = new HashMap<>();


    }
    public Collection<Integer> getVertices() {
        return this.lookupTable.keySet();
    }

    public void removeVertexEntry(Integer u, int[][] randomRRSetArray) {
        List<Object> entry = this.lookupTable.get(u);
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

        this.lookupTable.remove(u);
    }

    public void decrementCountForVertex(int u, int setID) {
        List<Object> entry = this.lookupTable.get(u);
        if(entry==null) return;
        int count = (int)entry.get(0);
        count--;
        entry.set(0, count);
        Collection<Integer> setsCovered = (Collection<Integer>)entry.get(1);
        setsCovered.remove((Integer) setID); //This actually removes the integer Object! Not at integer the index. The typecast ensures that.
    }

    public void incrementCountForVertex(Integer vertex, int randomRRSetID) {
        List<Object> list = this.lookupTable.get(vertex);
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
        this.lookupTable.put(vertex, list);
    }

    public int countForVertex(Integer u) {
        return (Integer)this.lookupTable.get(u).get(0);
    }
}
