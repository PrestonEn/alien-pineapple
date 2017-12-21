package brock.pe12nh;

import java.util.ArrayList;
import java.util.List;
import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.apache.commons.cli.ParseException;
import java.util.stream.Collectors;
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

    public Partition(AdjGraph g, int i){
        List<Integer> l = new ArrayList<>();
        l.add(i);
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
        int pA = 0;
        int pB = 0;
        int fixedSize = a.membership.size() + b.membership.size();
        ArrayList<Integer> aNew = new ArrayList<>();
        ArrayList<Integer> bNew = new ArrayList<>();
        boolean contA = true;
        boolean contB = true;

        // ugly case
        while(contA || contB){
            // both lists are still not traversed
            if(contA && contB && a.membership.get(pA) < b.membership.get(pB)){
                aNew.add(a.membership.get(pA));
                bNew.add(-1);
                pA++;
                if(pA == a.membership.size()){
                    contA = false;
                }
            }else if(contA && contB && a.membership.get(pA) > b.membership.get(pB)){
                bNew.add(b.membership.get(pB));
                aNew.add(-1);
                pB++;
                if(pB == b.membership.size()){
                    contB = false;
                }
            }else if(contA && !contB){
                aNew.add(a.membership.get(pA));
                bNew.add(-1);
                pA++;
                if(pA == a.membership.size()){
                    contA = false;
                }
            }else if(!contA && contB){
                bNew.add(b.membership.get(pB));
                aNew.add(-1);
                pB++;
                if(pB == b.membership.size()){
                    contB = false;
                }
            }
        }

        // TODO: bias the crossover
        for(int i=0; i<aNew.size(); i++){
            double roll = Main.randgen.nextDouble();
            if(roll < 0.5){
                int tmp = aNew.get(i);
                aNew.set(i, bNew.get(i));
                bNew.set(i, tmp);
            }
        }

        // filter
        a.membership = new ArrayList<Integer>(aNew.stream().filter(i -> i != -1).collect(Collectors.toList()));
        b.membership = new ArrayList<Integer>(bNew.stream().filter(i -> i != -1).collect(Collectors.toList()));
    }




}
