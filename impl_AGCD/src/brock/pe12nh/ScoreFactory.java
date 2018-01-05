package brock.pe12nh;

import java.util.ArrayList;
import java.util.List;
import com.brock.pe12nh.AdjGraph.AdjGraph;

/**
 * agcd was developed with 3 potential fitness functions. The third, silhouette width, was omitted
 * due to its lack of success in the test performed by the original author.
 */
public class ScoreFactory {
    public static double normCut(AdjGraph g, List<Integer> membership){
        double cutSize = g.getCutSize(membership);
        double vvAssoc = g.vvAssoc;

        double cutVk =  cutSize * vvAssoc;
        double assocVk = g.getPartitionAssoc(membership);
        double score = cutVk/(assocVk * (g.vvAssoc - assocVk));
        return score;
    }
}
