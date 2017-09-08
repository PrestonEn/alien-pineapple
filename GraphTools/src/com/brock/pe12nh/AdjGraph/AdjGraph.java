package com.brock.pe12nh.AdjGraph;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import java.io.IOException;
import java.util.Collection;
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


    public AdjGraph(String filePath) throws IOException {
        g = new DefaultGraph("g");
        FileSource fs = FileSourceFactory.sourceFor(filePath);

        fs.addSink(g);

        try {
            fs.begin(filePath);

            while (fs.nextEvents()) {
                // Optionally some code here ...
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
        genAdjMat();
        genDegree();
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

    public void writeGml(){


    }
}
