import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;
import org.w3c.dom.traversal.NodeIterator;

import java.util.*;

public class Individual implements Runnable {
    public int[] locus;
    public double score = 0d;
    public AdjGraph g;

    public Individual(int[] locus, AdjGraph g) {
        this.locus = locus;
        this.g = g;
        this.score = Modularity.getModularity(this.g, Individual.decode(this));
    }

    public Individual(AdjGraph g) {
        this.g = g;
        this.locus = new int[g.g.getNodeCount()];
        initGenes();
        this.score = Modularity.getModularity(this.g, Individual.decode(this));
    }

    public Individual(Individual i) {
        this.g = i.g;
        this.locus = i.locus.clone();
        this.score = i.score;
    }

    /**
     * Initilization limits selecting links between neighbours
     */
    private void initGenes() {
        for (int i = 0; i < locus.length; i++) {
            Iterator<Node> niter = g.g.getNode(i).getNeighborNodeIterator();
            ArrayList<Integer> nghIndex = new ArrayList<>();
            while (niter.hasNext()) {
                nghIndex.add(niter.next().getIndex());
            }
            this.locus[i] = nghIndex.get(Main.randgen.nextInt(nghIndex.size()));
        }
    }

    /**
     * linear time decoding procedure
     *
     * @param indi
     * @return
     */
    public static Solution decode(Individual indi) {
        int cluster_assign[] = new int[indi.locus.length];
        int prv[] = new int[indi.locus.length];
        for (int i = 0; i < indi.locus.length; i++) {
            cluster_assign[i] = -1;
        }
        int crnt_clstr = 1;

        for (int i = 0; i < indi.locus.length; i++) {
            int ctr = 0;
            if (cluster_assign[i] == -1) {
                cluster_assign[i] = crnt_clstr;
                int neigbour = indi.locus[i];
                prv[ctr] = i;
                ctr++;
                while (cluster_assign[neigbour] == -1) {
                    prv[ctr] = neigbour;
                    cluster_assign[neigbour] = crnt_clstr;
                    neigbour = indi.locus[neigbour];
                    ctr++;
                }
                if (cluster_assign[neigbour] != crnt_clstr) {
                    ctr--;
                    while (ctr >= 0) {
                        cluster_assign[prv[ctr]] = cluster_assign[neigbour];
                        ctr--;
                    }
                } else {
                    crnt_clstr++;
                }
            }
        }

        return new Solution(cluster_assign, indi.g);
    }


    public void repair() {
        for (int i = 0; i < locus.length; i++) {
            if (!g.adjMat[i][this.locus[i]]) {
                Iterator<Node> niter = g.g.getNode(i).getNeighborNodeIterator();
                ArrayList<Integer> nghIndex = new ArrayList<>();
                while (niter.hasNext()) {
                    nghIndex.add(niter.next().getIndex());
                }
                this.locus[i] = nghIndex.get(Main.randgen.nextInt(nghIndex.size()));
            }
        }
    }

    public static Individual crossover(Individual p1, Individual p2){
        int[] c1g = new int[p1.locus.length];
        for (int i = 0; i < p1.locus.length; i++) {
            boolean maskval = Main.randgen.nextDouble() <= 0.5 ? true : false;
            c1g[i] = maskval ? p1.locus[i] : p2.locus[i];
        }
        return new Individual(c1g, p1.g);
    }

    /**
     * Get a single string of locus indexes for use in crossover
     *
     * @return
     */
    public ArrayList<Integer> getClusterIndexs() {
        ArrayList<Integer> indexes = new ArrayList<>();
        boolean[] traveresed = new boolean[this.locus.length];
        int index = Main.randgen.nextInt(this.locus.length);
        while (traveresed[index] != true) {
            indexes.add(index);
            traveresed[index] = true;
            index = this.locus[index];
        }
        return indexes;
    }



    public void marginalMutate() {
        Solution c = Individual.decode(this);
        ArrayList<Integer> marginalGenes = MarginalExtractor.marginalNodes(this);
        ArrayList<Integer> neighbours;
        HashMap<Integer, ArrayList<Integer>> neighbourClusters;
        Set<Integer> labels;
        int bestLabel;
        int origLabel;
        double score;
        for (int i :
                marginalGenes) {
            //init for this node
            neighbourClusters = new HashMap<>();
            origLabel = c.membership[i];
            score = Double.MIN_VALUE;
            Iterator<Node> ni = this.g.g.getNode(i).getNeighborNodeIterator();
            neighbours = new ArrayList<>();
            labels = new HashSet<>();
            bestLabel = origLabel;
            // consider the current labeling
            neighbours.add(i);
            labels.add(origLabel);
            neighbourClusters.put(origLabel, new ArrayList<>());
            // get neighbourhood and associate neighbours with clusters
            while (ni.hasNext()) {
                int index = ni.next().getIndex();
                neighbours.add(index);
                labels.add(c.membership[index]);
                if (neighbourClusters.containsKey(c.membership[index])) {
                    neighbourClusters.get(c.membership[index]).add(index);
                } else {
                    neighbourClusters.put(c.membership[index], new ArrayList<>());
                    neighbourClusters.get(c.membership[index]).add(index);
                }
            }

            // identify best new candidate cluster
            for (int label :
                    labels) {
                c.membership[i] = label;
                double candScore = Modularity.getLocalScore(i, c, this.g);
                if (candScore > score) {
                    score = candScore;
                    bestLabel = label;
                }
            }

            this.locus[i] = neighbourClusters.get(bestLabel).get(Main.randgen.nextInt(neighbourClusters.get(bestLabel).size()));
        }
    }

    public void run() {
        score = Modularity.getModularity(this.g, Individual.decode(this));
    }

    public String getMembershipString() {
        String str = "";
        int[] m = Individual.decode(this).membership;
        for (int i = 0; i < m.length; i++) {
            str += Integer.toString(m[i]);
            if (i != m.length - 1)
                str += ",";

        }
        return str;
    }


    public String getLocusString() {
        String str = "";
        int[] m = this.locus;
        for (int i = 0; i < m.length; i++) {
            str += Integer.toString(m[i] + 1);
            if (i != m.length - 1)
                str += ",";
        }
        return str;
    }
}
