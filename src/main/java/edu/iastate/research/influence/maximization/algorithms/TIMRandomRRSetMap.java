package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.Vertex;

import java.util.*;

import static com.sun.tools.classfile.Opcode.get;

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

    public void removeVertexEntry(Integer u) {


//        List<Object> entryList = (List<Object>)this.lookupTable.get(u);
//
//        //Remove randomRRSet
//        List<Integer> randomRRSetsCovered = (List<Integer>) entryList.get(1);
//        Set<Vertex> vertices = this.lookupTable.keySet();
//        vertices.remove(u);
//        int i = 0;
//        for(Vertex vertex:vertices) {
//            List<Object> values = this.lookupTable.get(vertex);
//            //These are the RR Sets and number covered by this vertex. Look at the intersection and remove
//            List<Integer> rrSetsOfVertex = (List<Integer>)values.get(1);
//            int count = (Integer)values.get(0);
//            int numberOfSetsRemoved = 0;
//
//            for (Integer rrSet :
//                    randomRRSetsCovered) {
//                if(rrSetsOfVertex.contains(rrSet)) {
//                    rrSetsOfVertex.remove(rrSet);
//                    numberOfSetsRemoved++;
//                }
//            }
//            values.set(0, count - numberOfSetsRemoved);
//            if(i%10000==0) { System.out.println("Finished iteration " + i++);}
//        }
        this.lookupTable.remove(u);
    }

    public void incrementCountForVertex(Integer vertex, RandomRRSet randomRRSet) {
        List<Object> list = this.lookupTable.get(vertex);
        if(list==null) {
            list = new ArrayList();
            list.add(0);
            list.add(new ArrayList<Integer>());
        }
        int count = (Integer)list.get(0);
        count++;
        list.set(0, count);
        List<Integer> sets = (List<Integer>)list.get(1);
        sets.add(randomRRSet.getId());
        this.lookupTable.put(vertex, list);
    }

    public int countForVertex(Integer u) {
        return (Integer)this.lookupTable.get(u).get(0);
    }
}
