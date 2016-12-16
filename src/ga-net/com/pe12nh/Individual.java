package com.pe12nh;

/**
 * Created by preston on 2016-09-15.
 */
public abstract class Individual {

    int[] genes;

    double fitness;

    /**
     * generate new genes for this individual
     *
     */
    abstract void initGenes();

    /**
     * extract a ClusterSol from the individual 
     * @return
     */
    abstract ClusterSol decode();

    double getFitness(){
        return this.fitness;
    }

    int getGene(int pos){
        return this.genes[pos];
    }

    int getGeneCount() { return  this.genes.length; }

}



