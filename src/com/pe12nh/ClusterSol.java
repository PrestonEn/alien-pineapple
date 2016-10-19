package com.pe12nh;

import java.util.HashMap;

/**
 * Created by preston on 2016-09-15.
 */

public class ClusterSol {

    public HashMap<Integer, Integer> clusters;

    public ClusterSol(){
        clusters = new HashMap();
    }

    void updateClusterSol(int nodeNum, int cluster) {
        this.clusters.put(nodeNum, cluster);
    }

    double calcModularity() {
        double graphSum = 0.0;
        for (int i = 0; i < Main.n.nodeCount; i++) {
            for (int j = 0; j < Main.n.nodeCount; j++) {
                if (i != j) {
                    double dgre = Main.n.getNeighbours(i).size() +
                            Main.n.getNeighbours(j).size() / (2.0 / Main.n.edgeCount);
                    if (this.clusters.get(i) == this.clusters.get(j))
                        graphSum += dgre;
                }
            }
        }
        return (1.0 / 2.0 * Main.n.edgeCount) * graphSum;
    }

}


