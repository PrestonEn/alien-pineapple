package brock.pe12nh;

import java.util.List;
import com.brock.pe12nh.AdjGraph.AdjGraph;

/**
 * Created by preston on 2017-11-27.
 */
public class ScoreFactory {

    public static double normCut(AdjGraph g, List<Integer> membership){
        double cutVk = g.getCutSize(membership) * g.vvAssoc;
        double assocVk = g.getPartitionAssoc(membership);
        return cutVk/(assocVk * g.vvAssoc - assocVk);
    }

    public static double normMod(AdjGraph g, List<Integer> membership){
        double cut = g.getCutSize(membership);
        double assocVk = g.vvAssoc;
        double lterm = cut/assocVk;
        double rterm = (1 / (cut * g.vvAssoc)/(assocVk * g.vvAssoc - assocVk)) -1 ;
        return lterm * rterm;
    }


    public static double silwidth(AdjGraph g, int node){
        return  0d;
    }


}
