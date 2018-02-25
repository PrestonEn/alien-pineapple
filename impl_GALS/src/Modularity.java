import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;


public class Modularity {

    /**
     * Implementation of Newman 2006 modularity.
     * <p>
     * Given a graph, and a partitioning of nodes into clusters
     *
     * @param
     * @param
     * @return
     */
    public static double getModularity(AdjGraph g, Solution s) {
        double score = 0d;
        for(int i=0; i<s.membership.length; i++){
            score += getLocalScore(i,s,g);
        }
        return score * (1d/(2d*(double)g.g.getEdgeCount()));
    }


    public static double getLocalScore(int i, Solution s, AdjGraph g) {
        double score = 0.0;
        int clust = s.membership[i];
        ArrayList<Integer> members = s.groups.get(clust);
        for (int node : members) {
            int adj = g.adjMat[i][node] ? 1 : 0;
            score += (double)adj - (((double)g.degrees[i] * (double)g.degrees[node]) / (2d * (double)g.g.getEdgeCount()));
        }
        return score;
    }

}
