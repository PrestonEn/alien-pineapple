package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.io.IOException;
import java.util.Random;

/**
 * Created by speng on 1/7/2018.
 */
public class Test {

    public static Random randgen = new Random();
    public static AdjGraph g;
    public static SimilarityMatrix symMat;
    public static void main(String args[]){
        // get params
        try {
            g = new AdjGraph("../benchmark_gen/gml_files/real_networks/karate.gml");
            symMat = new SimilarityMatrix(g);
            //p.generationActions();
            System.out.println("test");
            Partition p = new Partition(g, 19);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
