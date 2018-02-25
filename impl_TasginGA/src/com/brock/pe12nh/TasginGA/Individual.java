package com.brock.pe12nh.TasginGA;

import java.util.*;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;

public class Individual implements Runnable {

    public int[] membership;
    public double score;
    public boolean scored;
    AdjGraph g;

    public Individual(AdjGraph g, double initBias) {
        this.g = g;
        membership = new int[g.g.getNodeCount()];
        initGenes(initBias);
        scored = true;
    }

    public Individual(AdjGraph g, int[] mem) {
        this.g = g;
        membership = Arrays.copyOf(mem, mem.length);
        scored = false;
    }

    public Individual(Individual i) {
        this.g = i.g;
        membership = Arrays.copyOf(i.membership, i.membership.length);
        this.score =i.score;
        scored = true;
    }



    /**
     * <p>Initialization of individuals is done by randomly by assigning each node a random cluster, bounded to
     * the number of nodes.</p>
     * <p>
     * <p>a random subset of nodes, making up the fraction defined by bias,
     * assigns their cluster id to all of their neighbours</p>
     *
     * @param bias: fraction of nodes to propagate cluster label to neighbours
     */
    private void initGenes(double bias) {
        int n = g.g.getNodeCount();
        // keep clusters bounded to number of nodes
        for (int i = 0; i < n; i++) {
            membership[i] = Main.randgen.nextInt(n - 1);
        }

        // after assigning ids,  propagate the id of some nodes to their neighbours
        int selectcount = (int) (bias * (double) n);
        for (int i = 0; i < selectcount; i++) {
            int j = Main.randgen.nextInt(n);
            Iterator<Node> niter = g.g.getNode(j).getNeighborNodeIterator();
            while (niter.hasNext()) {
                membership[niter.next().getIndex()] = membership[j];
            }
        }
        score = Modularity.getModularity(g, membership);
    }

    /**
     * From c1, select a random node, and its membership.
     * Assign that membership to all corresponding nodes in c2
     * <p>
     * Eg: We select node 0 in c1
     * c1 -> [1, 2, 3, 1, 1]
     * c2 -> [4, 3, 5, 6, 7]
     * ---------------------
     * c3 -> [1, 3, 5, 1, 1]
     *
     * @param c1
     * @param c2
     * @return
     */
    public static Individual oneWayCross(Individual c1, Individual c2) {
        // select a gene from c1
        int clust = c1.membership[Main.randgen.nextInt(c1.membership.length)];
        int[] newgenes = new int[c1.membership.length];
        for (int i = 0; i < c1.membership.length; i++) {
            if (c1.membership[i] == clust) {
                newgenes[i] = c1.membership[i];
            } else {
                newgenes[i] = c2.membership[i];

            }
        }
        Individual child = new Individual(c1.g, newgenes);
        return child;
    }

    /**
     * set a random gene to a random cluster
     */
    public void mutate(double mutrate) {
        HashSet<Integer> clusts = new HashSet<>();

        for (Integer i : this.membership) {
            clusts.add(i);
        }

        int item = Main.randgen.nextInt(clusts.size());
        int index = Main.randgen.nextInt(this.membership.length);
        int i = 0;
        for (Integer it :
                clusts) {
            if (i == item) {
                this.membership[index] = it;
                return;
            }
            i++;
        }
        scored = false;
    }

    /**
     * community cleaning procedure based on community variance
     *
     * @param portion
     * @param thold
     */
    public void cleanUp(double portion, double thold) {
        int b = (int) (portion * this.membership.length);
        for (int i = 0; i < b; i++) {
            int n = Main.randgen.nextInt(this.membership.length);
            Iterator<Node> niter = g.g.getNode(n).getNeighborNodeIterator();
            double nonCom = 0d;
            int com = this.membership[n];
            ArrayList<Integer> nClusts = new ArrayList<>();
            ArrayList<Integer> nInx = new ArrayList<>();

            // for each neighbour, if not in the same community as n,
            // nonCom++
            while (niter.hasNext()) {
                Node nn = niter.next();
                nClusts.add(this.membership[nn.getIndex()]);
                nInx.add(nn.getIndex());
                if (this.membership[nn.getIndex()] != com) {
                    nonCom += 1d;
                }
            }
            nonCom = nonCom / (double) g.g.getNode(n).getDegree();
            if (nonCom > thold) {
                int x = getMode(nClusts);
                this.membership[n] = x;
                for (Integer in : nInx) {
                    this.membership[in] = x;
                }
            }
        }
        scored = false;
    }

    private Integer getMode(ArrayList<Integer> a) {
        HashMap<Integer, Integer> count = new HashMap<>();
        for (Integer ai :
                a) {
            if (!count.containsKey(ai)) {
                count.put(ai, 1);
            } else {
                count.put(ai, count.get(ai) + 1);
            }
        }
        int i = 0;
        int v = 0;
        for (Integer key : count.keySet()) {
            if (count.get(key) > i) {
                i = count.get(key);
                v = key;
            }
        }
        return v;
    }

    @Override
    public void run() {
        if(!scored) {
            score = Modularity.getModularity(this.g, membership);
            scored = true;
        }
    }

    public void printMembership() {
        for (int i = 0; i < membership.length; i++) {
            System.out.print(membership[i] + ",");
        }
        System.out.println();
    }

    public String getMembershipString() {
        int[] lowest = new int[this.membership.length];
        HashMap<Integer, Integer> mapping = new HashMap<>();
        int count = 0;
        for (int i = 0; i < this.membership.length; i++) {
            if (mapping.containsKey(this.membership[i])) {
                lowest[i] = mapping.get(this.membership[i]);
            } else {
                mapping.put(this.membership[i], count);
                lowest[i] = count;
                count++;
            }
        }

        String str = "";
        for (int i = 0; i < lowest.length; i++) {
            str += Integer.toString(lowest[i]);
            if (i != lowest.length - 1)
                str += ",";

        }
        return str;
    }

}
