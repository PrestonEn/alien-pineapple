package com.brock.pe12nh.TasginGA;

import com.brock.pe12nh.AdjGraph.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Population {
    public boolean badCompare;
    ArrayList<Individual> pop;
    double elitePortion;

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
    public void scorePop(boolean parallel) throws InterruptedException {
        if (parallel) {
            ExecutorService exec = Executors.newFixedThreadPool(8);
            for (Runnable r :
                    pop) {
                exec.execute(r);
            }
            exec.shutdown();
            exec.awaitTermination(5, TimeUnit.SECONDS);
        } else {
            for (Individual r :
                    pop) {
                r.score = Modularity.getModularity(r.g, r.membership);
            }
        }
    }


    public void sortPop() {
        Collections.sort(pop, new IndividualComparator());
    }

    public void updateGen(boolean parallel) throws InterruptedException {
        sortPop();
        ArrayList<Individual> elitePop = new ArrayList<Individual>((int)(Main.popSize * Main.elitePortion));
        ArrayList<Individual> childPop = new ArrayList<Individual>((int)(Main.popSize));
        for (int i=0; i<(int)(Main.popSize * Main.elitePortion); i++){
            elitePop.add(0,pop.get((pop.size()-1)-i));
        }
        ArrayList<Double> roul = buildRouletteTable();
        while (childPop.size() < pop.size() - elitePop.size()){
            Individual c = Individual.oneWayCross(roulSelect(roul), roulSelect(roul));
            c.mutate(Main.mutRate);
            if (Main.randgen.nextDouble() < Main.cleanRate){
                c.cleanUp(Main.cleanPortion, Main.cleanThold);
            }
            childPop.add(c);
        }

        childPop.addAll(elitePop);
        pop = childPop;
        scorePop(true);

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
        return Collections.max(pop, new IndividualComparator());
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
                return new Individual(pop.get(i));
            }
        }
        return pop.get(pop.size() - 1);
    }

}
