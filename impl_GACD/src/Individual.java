import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by speng on 7/31/2017.
 */
public class Individual implements Runnable{
    public int[] locus;
    public double score = 0d;
    public AdjGraph g;

    public Individual(int[] locus, AdjGraph g){
        this.locus = locus;
        this.g = g;

    }

    public Individual(AdjGraph g){
        this.g = g;
        this.locus = new int[g.g.getNodeCount()];
        initGenes();
        this.repair();
        this.score = Modularity.getModularity(this.g, Individual.decode(this).membership);
    }

    /**
     * Initilization limits selecting links between neighbours
     */
    private void initGenes(){
        for (int i=0; i < locus.length; i++){
            int rand = Main.randgen.nextInt(locus.length);
            this.locus[i] =rand;
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
        //TODO
    }

    public void mutate(){
        int index = Main.randgen.nextInt(this.locus.length);
        Iterator<Node> nIter = this.g.g.getNode(index).getNeighborNodeIterator();
        ArrayList<Integer> nIndexs = new ArrayList<>();
        while (nIter.hasNext()){
            nIndexs.add(nIter.next().getIndex());
        }

        if (nIndexs.size() > 1){
            this.locus[index] = nIndexs.get(Main.randgen.nextInt(nIndexs.size()));
        }

    }

    public void run(){
        score = Modularity.getModularity(this.g, Individual.decode(this).membership);
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
                str += ",";
        }
        return str;
    }
}
