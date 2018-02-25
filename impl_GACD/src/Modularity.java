import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by speng on 6/26/2017.
 */
public class Modularity {

    /**
     * Implementation of Newman 2006 modularity.
     *
     * Given a graph, and a partitioning of nodes into clusters
     *
     * @param
     * @param
     * @return
     */
    public static double getModularity(AdjGraph g, Solution s) {
        int m = g.edgeSet.size();
        int clusts = IntStream.of(s.membership).max().getAsInt() + 1;
        int[] e = new int[clusts];
        int[] a = new int[clusts];

        for (Edge i : g.edgeSet) {
            int c0 = s.membership[i.getNode0().getIndex()];
            int c1 = s.membership[i.getNode1().getIndex()];
            if (c0 == c1) {
                e[c0] += 2.0;
            }
            a[c0] += 1.0;
            a[c1] += 1.0;
        }
        double modu = 0.0;

        for (int i = 0; i < clusts; i++) {
            double tmp = (double) a[i] / 2.0 / (double) m;
            modu += (double) e[i] / 2.0 / (double) m;
            modu -= tmp * tmp;
        }
        return modu;
    }


    public static double getLocalScore(int i, Solution s, AdjGraph g){
        double score = 0.0;
        int clust = s.membership[i];
        ArrayList<Integer> members = s.groups.get(clust);
        for(int node: members){
            int adj = g.adjMat[i][node] ? 1 : 0;
            score += adj - ((g.degrees[i]*g.degrees[node])/(2*g.g.getEdgeCount()));
        }
        return score;
    }

}
