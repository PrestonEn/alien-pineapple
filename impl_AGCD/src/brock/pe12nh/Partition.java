package brock.pe12nh;

import java.util.ArrayList;
import java.util.List;
import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.apache.commons.cli.ParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.fitness = ScoreFactory.normCut(this.g, this.membership);
    }

    public Partition(AdjGraph g, int i){
        List<Integer> l = new ArrayList<>();
        l.add(i);
        this.membership = l;
        this.g = g;
        this.fitness = ScoreFactory.normCut(this.g, this.membership);
    }

    public Partition(Partition p){
        membership = new ArrayList<>();

        for (int i:
             p.membership) {
            this.membership.add(i);
        }

        this.g = p.g;
        this.fitness = p.fitness;
    }


    public void score(){
        if(this.membership.size() != 0) {
            this.fitness = ScoreFactory.normCut(g, membership);
        }else{
            this.fitness = 0;
        }
    }

    /**
     * Variable length bias crossver.
     *
     * selected individuals will be mapped to an equal length, have the crossover applied,
     * and filtered to the new size
     * @param a
     * @param b
     * @param bias
     */
    public static void crossover(Partition a, Partition b, double bias){
        int pA = 0;
        int pB = 0;
        ArrayList<Integer> aNew = new ArrayList<>();
        ArrayList<Integer> bNew = new ArrayList<>();
        boolean contA = true;
        boolean contB = true;

        // ugly case statement could get cleaned up
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

        // TODO: bias the crossover to favor small partitons
        // SEE THE PAPER
        System.out.format("a size: %d a bias: %f\tb size: %d b bias:%f\n",
                a.membership.size(), Partition.calcBias(a, b, bias),
                b.membership.size(), Partition.calcBias(b, a, bias));

        for(int i=0; i<aNew.size(); i++){
            if(aNew.get(i) == -1){
                if(Main.randgen.nextDouble() < Partition.calcBias(a, b, bias)) {
                    aNew.set(i, bNew.get(i));
                    bNew.set(i, -1);
                }
            }else{
                if(Main.randgen.nextDouble() < Partition.calcBias(b, a, bias)) {
                    bNew.set(i, aNew.get(i));
                    aNew.set(i, -1);
                }
            }
        }

        // filter
        a.membership = new ArrayList<Integer>(aNew.stream().filter(i -> i != -1).collect(Collectors.toList()));
        b.membership = new ArrayList<Integer>(bNew.stream().filter(i -> i != -1).collect(Collectors.toList()));
        a.score();
        b.score();
        return;
    }

    private static double calcBias(Partition p1, Partition p2, Double puni){
        double sizeR = (double)p1.membership.size()/((double)p1.membership.size() + (double)p2.membership.size());
        double mulTerm = (1 - (2 * puni));
        return  puni + mulTerm * sizeR;
    }


    public void printMembership(){
        for (int i:
             this.membership) {
            System.out.print(" " + (i+1));
        }
        System.out.print('\n');
    }



}
