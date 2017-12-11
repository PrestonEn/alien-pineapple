package brock.pe12nh;

import java.util.ArrayList;
import java.util.List;
import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.apache.commons.cli.ParseException;

/**
 * Basic individual of the agglomerative GA. Each one is initialized to be a single node.
 *
 * Responsible for storage and transformation of cluster membership
 */
public class Partition {
    List<Integer> membership;
    AdjGraph g;
    double fitness;

    public Partition(AdjGraph g, List<Integer> i){
        this.membership = i;
        this.g = g;
    }

    public void score(){
        this.fitness = ScoreFactory.normCut(g, membership);
    }

    /**
     * Variable length bias crossver.
     *
     * selected individuals will be mapped
     * @param a
     * @param b
     * @param bias
     */
    public static void crossover(Partition a, Partition b, double bias){
        int index = 0;
        int minSize = Integer.min(a.membership.size(), b.membership.size());
        int fixedSize = a.membership.size() + b.membership.size();
        ArrayList<Integer> aExpanded = new ArrayList<>();
        ArrayList<Integer> bExpanded = new ArrayList<>();

        //map to fixed length

        //crossover

        //filter

        //save respective clusters
    }




}
