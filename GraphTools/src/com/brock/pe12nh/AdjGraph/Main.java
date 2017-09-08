package com.brock.pe12nh.AdjGraph;
import org.graphstream.graph.Edge;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        AdjGraph test = new AdjGraph("C:\\Users\\speng\\GraphTools\\karate.gml");
        Collection<Edge> es = test.g.getEdgeSet();
        for (Edge e:
             es) {
            System.out.println("Node " + e.getSourceNode().getIndex() + " Node " + e.getTargetNode().getIndex());
        }
    }
}
