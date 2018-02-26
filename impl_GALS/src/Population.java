import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Population {
    ArrayList<Individual> pop;
    AdjGraph g;

    public Population(int p, AdjGraph g) {
        this.g = g;
        this.pop = new ArrayList<>();
        for (int i = 0; i < p; i++) {
            pop.add(new Individual(g));

        }
        try {
            Population.scorePop(pop, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sortPop() {
        Collections.sort(pop, new IndividualComparator());
    }

    public Individual getMax() {
        return Collections.max(pop, new IndividualComparator());
    }

    public static void scorePop(ArrayList<Individual> pop, boolean parallel) throws InterruptedException {
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

    /**
     *
     * @throws InterruptedException
     */
    public void updatePop() throws InterruptedException {
        sortPop();
        ArrayList<Double> weightList = buildRouletteTable();
        ArrayList<Individual> newPop = new ArrayList<>(Main.offSize);
        Individual p1, p2;
        while (newPop.size() < Main.offSize) {
            //select 2 parents via roulette
            p1 = this.roulSelect(weightList);
            p2 = this.roulSelect(weightList);
            //try to crossover
            Individual child = Individual.crossover(p1, p2);
            child.marginalMutate();
            newPop.add(child);
            //insert into new pop
        }

        this.scorePop(newPop, true);
        newPop.addAll(this.pop);
        Collections.sort(newPop, new IndividualComparator());
        this.pop = new ArrayList<>(newPop.subList(newPop.size() - pop.size(), newPop.size()));
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
                return new Individual(pop.get(i));
            }
        }
        return new Individual(pop.get(pop.size() - 1));
    }

}