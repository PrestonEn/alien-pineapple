package com.brock.pe12nh.AdjGraph;
import org.graphstream.graph.Edge;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        AdjGraph test = new AdjGraph("../gml_files/real_networks/karate.gml");
        Collection<Edge> es = test.g.getEdgeSet();
        ArrayList<Integer> a = new ArrayList<>();
        System.out.println(test.getCutSize(a));

    }
}
