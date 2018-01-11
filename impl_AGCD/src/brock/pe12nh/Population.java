package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Population {

    private class BreedingPair{
        Partition p1;
        Partition p2;
    }

    ArrayList<Partition> pop;
    ArrayList<Double> partScore;
    private PartitionLookup lookup;
    AdjGraph g;
    boolean postiveChange;
    double populationScore;
    int convergeCount = 0;

    /**
     * creates initial population with each node stored in its own partition
     * @param g
     */
    public Population(AdjGraph g){
        postiveChange = false;
        populationScore = -1d;
        this.g = g;
        pop = new ArrayList<>();
        Iterator<Node> nodes = g.g.getNodeIterator();

        while(nodes.hasNext()){
            pop.add(new Partition(g,Integer.parseInt(nodes.next().getId())));
        }
        lookup = new PartitionLookup(this);
    }

    public void generationActions(){

        // stopping condition: if no change after x tries, give up
        while(convergeCount < convergeAttempts) {

            postiveChange = false;

            // select parents
            BreedingPair bp = selectParents();

            // clone them
            Partition c1 = new Partition(bp.p1);
            Partition c2 = new Partition(bp.p2);

            System.out.print("parent1:");
            bp.p1.printMembership();
            System.out.print("parent2:");
            bp.p2.printMembership();


            Partition.crossover(c1, c2, Main.bias);

            double sp = scorePartitions(Arrays.asList(bp.p1, bp.p2));
            double sc = scorePartitions(Arrays.asList(c1, c2));

            if (sc < sp) {
                postiveChange = true;
                // replace the parents with the children
                BreedingPair c = new BreedingPair();
                c.p1 = c1;
                c.p2 = c2;
                updateParents(bp, c);
            }

            if (!postiveChange) {
                convergeCount++;
            } else {
                convergeCount = 0;

            }
        }
    }

    /**
     * Selection of 2 parents for crossover
     *
     * P1: randomly activate a node
     *
     * p2: If P1 has neighbours in other clusters, chose one randomly via tournament on similarity,
     * get the partition it belongs to
     *
     * @return BreedingPair
     */
    public BreedingPair selectParents(){

        BreedingPair bp = new BreedingPair();
        boolean selected = false;

        // while valid parents have not been selected
        while(!selected) {
            // activate a random node
            int p1Id = Main.randgen.nextInt(g.adjMat.length);
            Partition activePart = this.lookup.getPartition(p1Id);

            // get the neighbourhood
            ArrayList<Node> neigh = new ArrayList<>();
            g.g.getNode(p1Id).getNeighborNodeIterator().forEachRemaining(neigh::add);

            //take difference of members
            neigh.removeAll(activePart.membership);
            int p2id;
            // we need inter cluster connections to use
            if(neigh.size() > 0){
                selected = true;
                bp.p1 = activePart;

                // get sim score of inter cluster neighbours
                // and treat as tournament (keep the best)

                p2id = Integer.parseInt(neigh.get(Main.randgen.nextInt(neigh.size())).getId());
                bp.p2 = this.lookup.getPartition(p2id);
                if(bp.p2 == bp.p1)
                    selected = false;
            }

        }
        return bp;
    }

    /**
     * Because scores are normalized to the size of the partitions, use a weighted avg to score the
     * population
     *
     * @param subset
     * @return
     */
    private double scorePartitions(List<Partition> subset){
        double weightedAvg = 0.0;
        for(Partition p: subset){
            weightedAvg += p.fitness * (double)p.membership.size();
        }
        return weightedAvg/(double)g.adjMat.length;
    }

    /**
     * identify and remove partitions from parent set, insert new children
     * and update partition lookup
     * @param p
     * @param c
     */
    private void updateParents(BreedingPair p,  BreedingPair c){
        // delete the old parent
        int p1_index = this.pop.indexOf(p.p1);
        this.pop.remove(p1_index);
        if(c.p1.membership.size() > 0){
            // insert the new child and update lookup
            this.pop.add(c.p1);
            this.lookup.updatePartition(c.p1);
        }

        int p2_index = this.pop.indexOf(p.p2);
        this.pop.remove(p2_index);
        if(c.p2.membership.size() > 0){
            // insert the new child and update lookup
            this.pop.add(c.p2);
            this.lookup.updatePartition(c.p2);
        }
    }



    public void printPop(){
        for (Partition p:
             this.pop) {
            p.printMembership();
        }
    }

    public int[] buildSolution(){
        int[] membership = new int[g.g.getNodeCount()];
        int c = 0;
        for (Partition p:
                this.pop) {
            for(int i: p.membership){
                membership[i] = c;
            }
            c++;
        }

        return membership;
    }
}
