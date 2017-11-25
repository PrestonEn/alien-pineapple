
import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Edge;

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
    public static double getModularity(AdjGraph g, int[] membership) {
        int m = g.edgeSet.size();
        int clusts = IntStream.of(membership).max().getAsInt() + 1;
        int[] e = new int[clusts];
        int[] a = new int[clusts];

        for (Edge i : g.edgeSet) {
            int c0 = membership[i.getNode0().getIndex()];
            int c1 = membership[i.getNode1().getIndex()];
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
}