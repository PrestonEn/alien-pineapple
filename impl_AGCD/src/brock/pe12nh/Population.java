package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;

public class Population {

    private class BreedingPair{
        Partition p1;
        Partition p2;
    }

    ArrayList<Partition> pop;
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
        while(convergeCount < 100) {
            postiveChange = false;

            // select parents
            BreedingPair bp = selectParents();

            // clone them
            Partition c1 = new Partition(bp.p1);
            Partition c2 = new Partition(bp.p2);
            Partition.crossover(c1, c2, 0.5);

            double sp = scorePartitions(Arrays.asList(bp.p1, bp.p2));
            double sc = scorePartitions(Arrays.asList(c1, c2));
            if (sc > sp) {
                postiveChange = true;
                // replace the parents with the children
                BreedingPair c = new BreedingPair();
                c.p1 = c1;
                c.p2 = c2;
                updateParents(bp, c);
                // update lookup
            }

            if (!postiveChange) {
                convergeCount++;
            } else {
                convergeCount = 0;

            }
        }
    }

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

            // we need inter cluster connections to use
            if(neigh.size() > 0){
                selected = true;
                bp.p1 = activePart;

                // get sim score of inter cluster neighbours
                // and treat as tournament (keep the best)
                double bestSym = -1;
                int p2id = -1;
                for (Node n:
                     neigh) {
                    double sym = Main.symMat.getSim(p1Id, Integer.parseInt(n.getId()));
                    if(sym > bestSym){
                        bestSym = sym;
                        p2id = Integer.parseInt(n.getId());
                    }
                }
                bp.p2 = this.lookup.getPartition(p2id);
            }

        }
        return bp;
    }

    private double scorePartitions(List<Partition> subset){
        double weightedAvg = 0.0;
        for(Partition p: subset){
            weightedAvg += p.fitness * (double)p.membership.size();
        }
        return weightedAvg/(double)g.adjMat.length;
    }

    private void updateParents(BreedingPair p,  BreedingPair c){

    }

}
