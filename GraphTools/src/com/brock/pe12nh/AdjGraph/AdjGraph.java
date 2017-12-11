package com.brock.pe12nh.AdjGraph;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;


/**
 * Provide a wrapper for graphstream reading and writing,
 * as well as creating adjacency matrix for fast access
 *
 */
public class AdjGraph{

    public Graph g;
    public boolean[][] adjMat;
    public int[] degrees;
    public Collection<Edge> edgeSet;
    public List<Integer> nodeIdSet;
    public int vvAssoc;

    public AdjGraph(String filePath) throws IOException {
        g = new DefaultGraph("g");
        FileSource fs = FileSourceFactory.sourceFor(filePath);

        fs.addSink(g);

        try {
            fs.begin(filePath);

            while (fs.nextEvents()) {
            }
        } catch( IOException e) {
            e.printStackTrace();
        }

        try {
            fs.end();
        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(g);
        }

        System.out.println(g.getNodeCount());
        edgeSet = g.getEdgeSet();
        nodeIdSet = this.buildNodeIdSet();
        genAdjMat();
        genDegree();
        this.vvAssoc = this.getVVAssoc();

    }

    /**
     * Taken from graphstream documentation
     */
    private void genAdjMat(){
        int n = g.getNodeCount();
        adjMat = new boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adjMat[i][j] = g.getNode(i).hasEdgeBetween(j) ? true : false;
    }

    private void genDegree(){
        degrees = new int[g.getNodeCount()];
        for(int i=0; i<g.getNodeCount(); i++){
            degrees[i] = g.getNode(i).getDegree();
        }
    }

    private List buildNodeIdSet(){
        List<Integer> s = new ArrayList<>();
        for (Node n:
             g.getNodeSet()) {
            s.add(Integer.parseInt(n.getId()));
        }
        return s;
    }

    /**
     * returns the cut size for the given subgraph nodes to the total node set
     * @param partA
     * @return
     */
    public int getCutSize(List<Integer> partA){
        Set<Integer> V = new HashSet<>(this.nodeIdSet);
        System.out.println(V.size());
        V.removeAll(partA);
        int sum = 0;
        for (Integer nodeA:
             partA) {
            for (Integer nodeB:
                 V) {
                sum += this.adjMat[nodeA][nodeB] ? 1 : 0;
            }
        }
        return sum;
    }

    /**
     *
     * @return
     */
    private int getVVAssoc(){
        int sum = 0;
        for (int i=0; i<this.adjMat.length; i++){
            for (int j=0; j < this.adjMat[i].length; j++){
                sum += this.adjMat[i][j] ? 1 : 0;
            }
        }
        return sum;
    }

    /** Associativity of a partiotion in relation to the vertex set of the graph
     *
     * @param partA
     * @return
     */
    public int getPartitionAssoc(List<Integer> partA){
        int sum = 0;
        for(Integer i: partA){
            for(int j=0; j<this.adjMat.length; j++){
                sum += this.adjMat[i][j] ? 1 : 0;
            }
        }
        return sum;
    }
}
