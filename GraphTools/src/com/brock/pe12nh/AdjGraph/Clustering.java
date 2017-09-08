package com.brock.pe12nh.AdjGraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by speng on 6/26/2017.
 */
public class Clustering {

    Map<Integer, List<Integer>> clusters;

    public Clustering(int[] mem){
        getClusters(mem);
    }

    public void getClusters(int[] membership){

        Map<Integer, List<Integer>> clustering = new HashMap<Integer, List<Integer>>();
        //for each index
        for(int i=0; i<membership.length; i++){
            if(!clustering.containsKey(membership[i])){
                clustering.put(membership[i], new LinkedList<Integer>());
                clustering.get(membership[i]).add(i);
            }else{
                clustering.get(membership[i]).add(i);
            }
        }
        this.clusters = clustering;
    }

}
