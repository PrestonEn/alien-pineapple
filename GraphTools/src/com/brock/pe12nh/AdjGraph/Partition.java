package com.brock.pe12nh.AdjGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic individual of the agglomerative GA. Each one is initialized to be a single node.
 *
 * Responsible for storage and transformation of cluster membership
 */
public class Partition {
    List<Integer> membership;
    AdjGraph g;

    public Partition(AdjGraph g, List<Integer> i){
        this.membership = i;
        this.g = g;
    }

    public void score(){

    }

    public List<Integer> getExpanded(){

    }

}
