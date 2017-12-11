package com.brock.pe12nh.TasginGA;

import com.brock.pe12nh.AdjGraph.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

/**
 * Created by speng on 7/4/2017.
 */
public class Population {
    ArrayList<Individual> pop;
    double elitePortion;
    public boolean badCompare;

    /**
     * @param size    populaiton size
     * @param g       graph
     * @param ep      elite portion
     * @param indBias individual init bias
     */
    public Population(int size, AdjGraph g, double ep, double indBias) {
        badCompare = false;
        pop = new ArrayList<>();
        elitePortion = ep;
        for (int i = 0; i < size; i++) {
            pop.add(new Individual(g, indBias));
        }
    }

    /**
     * @param parallel use threadpool exection
     */
    public void scorePop(boolean parallel) {
        if (parallel) {
            ExecutorService exec = Executors.newFixedThreadPool(10);
            for (Runnable r :
                    pop) {
                exec.execute(r);
            }
            exec.shutdown();
            while (!exec.isShutdown()) {
            }
        } else {
            for (Individual r :
                    pop) {
                r.score = Modularity.getModularity(r.g, r.membership);
            }
        }
    }

    /**
     *
     */
    public void sortPop() {
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

    /**
     * Genetic algorithm main loop call
     */
    public void updateGen(boolean parallel) {
        //System.out.println();
        scorePop(parallel);        // score the population
        sortPop();        // sort the population by score, increasing


        // create normalized fitness list for roulette
        ArrayList<Double> weightList = buildRouletteTable();
        // new population
        ArrayList<Individual> newPop = new ArrayList<>();
        ArrayList<Double> newPopScores = new ArrayList<>(); //debugging

        int eCount = (int) ((double) pop.size() * elitePortion);

        // add portion of elites to new pop
        for (int i = 0; i < eCount; i++) {
            newPop.add(pop.get(pop.size() - (i+1)));
            newPopScores.add(pop.get(pop.size() - (i+1)).score);
        }

        // child selection
        while (newPop.size() < pop.size()) {
            Individual p1 = this.roulSelect(weightList);
            Individual p2 = this.roulSelect(weightList);
            Individual newc = Individual.oneWayCross(p1, p2);

            if (Main.randgen.nextDouble() <= Main.mutRate) {
                newc.mutate();
            }

            if (Main.randgen.nextDouble() <= Main.cleanRate){
                newc.cleanUp(Main.cleanPortion, Main.cleanThold);
            }
            newPop.add(newc);
            newPopScores.add(newc.score);
        }


        pop = newPop;
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

    /**
     * @return
     */
    public Individual getBestInd() {
        Collections.sort(pop, (Individual p1, Individual p2) ->
                Double.compare(p1.score, p2.score));
        return pop.get(pop.size() - 1);
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


    /**
     * Proportional fitness selection
     *
     * @param
     * @return reference to selected individual
     */
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
