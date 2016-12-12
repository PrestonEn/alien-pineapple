package com.pe12nh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by preston on 2016-09-15.
 */
public class LocusIndividual extends Individual {

    @Override
    void initGenes() {


    }

    // decodes the solution into groups
    public ClusterSol decode(){
        List<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
        int[] grouping = new int[this.getGeneCount()];
        for (int i = 0; i < grouping.length; i++)
            grouping[i] = -1;

        int newestGroup = 0;

        for (int i = 0; i < this.getGeneCount(); i++) {
            int from = i;
            int to = this.getGene(i);
            //if the grouping for the to node is not yet assigned
            if (grouping[to] == -1){
                //if the from node has no grouping then make the pair in the same group
                if (grouping[from] == -1){
                    grouping[from] = newestGroup;
                    grouping[to] = newestGroup;
                    newestGroup++;
                }
                //otherwise add the to node to the group of the from node
                else{
                    grouping[to] = grouping[from];
                }
            }
            //if the to node already has a group
            else if (grouping[to] != -1){
                //if the 2 nodes are not in the same group
                if (grouping[to] != grouping[from]){
                    int mergingFrom = grouping[from];
                    int mergingTo = grouping[to];
                    for(int j = 0; j <= i; j++){
                        if (grouping[j] == mergingFrom)
                            grouping[j] = mergingTo;
                    }
                }
            }
        }
        ClusterSol clusters = new ClusterSol();
        for (int i = 0; i < grouping.length; i++) {
            clusters.updateClusterSol(i, grouping[i]);
        }

        return clusters;
    }
}
