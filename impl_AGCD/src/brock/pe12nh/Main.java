package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        AdjGraph g = null;
        try {
            g = new AdjGraph("../gml_files/real_networks/karate.gml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
