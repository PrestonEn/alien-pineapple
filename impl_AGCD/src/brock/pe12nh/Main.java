package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

        public static Random randgen = new Random();
        public static AdjGraph g;
        public static SimilarityMatrix symMat;
    public static void main(String args[]){
        // get params
        try {
            g = new AdjGraph("../gml_files/real_networks/karate.gml");
            symMat = new SimilarityMatrix(g);
            Population p = new Population(g);
            p.generationActions();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
