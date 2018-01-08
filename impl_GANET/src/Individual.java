import com.brock.pe12nh.AdjGraph.AdjGraph;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Individual implements Runnable{
    public int[] locus;
    public double score = 0d;
    public AdjGraph g;

    public Individual(int[] locus, AdjGraph g){
        this.locus = locus;
        this.g = g;
        this.score = CommunityScore.CommunityScore(Individual.decode(this), Main.powerVal);
    }

    public Individual(AdjGraph g){
        this.g = g;
        this.locus = new int[g.g.getNodeCount()];
        initGenes();
        this.score = CommunityScore.CommunityScore(Individual.decode(this), Main.powerVal);
    }

    /**
     * copy constructor
     * @param i
     */
    public Individual(Individual i){
        this.locus = i.locus.clone();
        this.score = i.score;
        this.g = i.g;
    }

    /**
     * Initilization limits selecting links between neighbours
     */
    private void initGenes(){

        for (int i=0; i < locus.length; i++){
            ArrayList<Integer> neigs = new ArrayList<>();
            Iterator<Node> niter = this.g.g.getNode(i).getNeighborNodeIterator();
            while (niter.hasNext()){
                neigs.add(niter.next().getIndex());
            }

            this.locus[i] = neigs.size() > 0 ? neigs.get(Main.randgen.nextInt(neigs.size())) : i;
        }
    }

    /**
     * linear time decoding procedure
     * @param indi
     * @return
     */
    public static Solution decode(Individual indi){
        int cluster_assign[] = new int[indi.locus.length];
        int prv[] = new int[indi.locus.length];
        for(int i=0; i<indi.locus.length; i++){
            cluster_assign[i] = -1;
        }
        int crnt_clstr = 1;

        for(int i=0; i < indi.locus.length; i++){
            int ctr = 0;
            if(cluster_assign[i] == -1){
                cluster_assign[i] = crnt_clstr;
                int neigbour = indi.locus[i];
                prv[ctr] = i;
                ctr++;
                while (cluster_assign[neigbour] == -1){
                    prv[ctr] = neigbour;
                    cluster_assign[neigbour] = crnt_clstr;
                    neigbour = indi.locus[neigbour];
                    ctr++;
                }
                if(cluster_assign[neigbour] != crnt_clstr){
                    ctr--;
                    while (ctr >= 0){
                        cluster_assign[prv[ctr]] = cluster_assign[neigbour];
                        ctr--;
                    }
                }else {
                    crnt_clstr++;
                }
            }
        }

        return new Solution(cluster_assign, indi.g);
    }



    public static Individual crossover(Individual p1, Individual p2){
        int[] c1g = new int[p1.locus.length];
        for (int i = 0; i < p1.locus.length; i++) {
            boolean maskval = Main.randgen.nextDouble() <= 0.5 ? true : false;
            c1g[i] = maskval ? p1.locus[i] : p2.locus[i];
        }
        return new Individual(c1g, p1.g);
    }

    public void mutate(){
        int i = Main.randgen.nextInt(this.locus.length);
        Iterator<Node> nIter = this.g.g.getNode(i).getNeighborNodeIterator();
        ArrayList<Integer> nIndexs = new ArrayList<>();
        while (nIter.hasNext()) {
            nIndexs.add(nIter.next().getIndex());
        }

        if (nIndexs.size() > 0) {
            this.locus[i] = nIndexs.get(Main.randgen.nextInt(nIndexs.size()));
        }
    }

    public void run(){
        score =
                CommunityScore.CommunityScore(Individual.decode(this), Main.powerVal);
    }

    public String getMembershipString(){
        String str = "";
        int[] m = Individual.decode(this).membership;
        for (int i = 0; i < m.length; i++) {
            str += Integer.toString(m[i]);
            if (i != m.length-1)
                str += ",";

        }
        return str;
    }


    public String getLocusString(){
        String str = "";
        int[] m = this.locus;
        for (int i = 0; i < m.length; i++) {
            str += Integer.toString(m[i]+1);
            if (i != m.length-1)
                str += "\n";
        }
        return str;
    }
}
