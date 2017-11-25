import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by speng on 7/31/2017.
 */
public class Population {

    ArrayList<Individual> pop;
    AdjGraph g;
    public Population(int p, AdjGraph g){
        this.g = g;
        this.pop = new ArrayList<>();
        for (int i = 0; i < p; i++) {
            pop.add(new Individual(g));
        }
    }

    public void sortPop(){
        try {
            Collections.sort(pop, new Comparator<Individual>() {
                @Override

                public int compare(Individual o1, Individual o2) {

                    if (o1.score > o2.score) {
                        return 1;
                    } else if (o1.score < o2.score) {
                        return -1;
                    }
                    return 0;
                }
            });
        } catch (IllegalArgumentException e) {
            //System.out.print("Contract exception");
        }
    }


    public void scorePop(boolean parallel) throws InterruptedException {
        if (parallel) {
            ExecutorService exec = Executors.newFixedThreadPool(10);
            for (Runnable r :
                    pop) {
                exec.execute(r);
            }
            exec.shutdown();
            exec.awaitTermination(2, TimeUnit.SECONDS);

        } else {
            for (Individual r :
                    pop) {
                 r.run();
            }
        }
    }
    public void updatePop() throws InterruptedException {
        this.scorePop(false);
        sortPop();
        ArrayList<Double> weightList = buildRouletteTable();
        ArrayList<Individual> newPop = new ArrayList<>();

        // get the elites
        int eCount = (int) ((double) pop.size() * Main.elitePortion);
        for (int i = 0; i < eCount; i++) {
            newPop.add(pop.get(pop.size() - (i+1)));
        }

        // (produce popSize * crossover rate) individuals through crossover
        for (int i=0; i < (int)(Main.popSize * Main.crossoverRate); i++) {
            Individual p1 = this.roulSelect(weightList);
            Individual p2 = this.roulSelect(weightList);

            Individual newc = Individual.crossover(p1, p2);
            if (Main.randgen.nextDouble() <= Main.mutRate)
                newc.mutate();
            newPop.add(newc);
        }

        //roulette select the rest
        while (newPop.size() < Main.popSize){
            Individual i = new Individual(this.roulSelect(weightList));
            if (Main.randgen.nextDouble() <= Main.mutRate)
                i.mutate();
            newPop.add(i);
        }

        this.pop = newPop;
    }

    /**
     * @return
     */
    public double getPopTotal() {
        double total = 0.0;
        for (Individual i : pop) {
            total += i.score;
        }
        return total;
    }

    /**
     * @return
     */
    public double getPopAvg() {
        double total = 0.0;
        for (Individual i :
                pop) {
            total += i.score;
        }
        return total / (double) pop.size();
    }

    private ArrayList<Double> buildRouletteTable() {
        double totalFit = this.getPopTotal();
        double prevProb = 0d;
        ArrayList<Double> indexList = new ArrayList<Double>();
        for (Individual i : pop) {
            double score = prevProb + (i.score / totalFit);
            indexList.add(score);
            prevProb = score;
        }
        return indexList;
    }

    private Individual roulSelect(ArrayList<Double> weightList) {
        double weightSum = 0d;
        for (int i = 0; i < weightList.size(); i++) {
            weightSum += weightList.get(i);
        }
        double val = Main.randgen.nextDouble() * weightSum;
        for (int i = 0; i < weightList.size(); i++) {
            val -= weightList.get(i);
            if (val <= 0d) {
                return pop.get(i);
            }
        }

        return pop.get(pop.size() - 1);
    }

}
