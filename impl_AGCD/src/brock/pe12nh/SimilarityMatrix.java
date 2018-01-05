package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.*;

/**
 */
public class SimilarityMatrix {
    private double[][] simMat;
    private AdjGraph g;

    public SimilarityMatrix(AdjGraph g){
        simMat = new double[g.nodeIdSet.size()][g.nodeIdSet.size()];
        for(int i = 0; i < simMat.length; i++){
            for (int j = 0; j < simMat.length; j++){
                Set<Integer> vN = new HashSet<>();
                Set<Integer> wN = new HashSet<>();

                g.g.getNode(i).getNeighborNodeIterator().forEachRemaining(n ->
                        vN.add(Integer.parseInt(n.getId())));
                g.g.getNode(j).getNeighborNodeIterator().forEachRemaining(n ->
                        wN.add(Integer.parseInt(n.getId())));

                Set<Integer> vNc = new HashSet<>(vN); //neighborhood of v
                Set<Integer> wNc = new HashSet<>(wN); //neighborhood of w

                vN.addAll(wNc); //union
                wN.retainAll(vNc); //intersection
                simMat[i][j] = wN.size()/(double)vN.size();
            }
        }
    }

    public double getSim(int v, int w){
        return simMat[v][w];
    }
}
