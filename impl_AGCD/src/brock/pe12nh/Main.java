package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

        public static Random randgen = new Random();
        public static AdjGraph g;
        public static SimilarityMatrix symMat;
    public static void main(String args[]){
        // get params
        try {

            g = new AdjGraph("D:/alien-pineapple/benchmark_gen/gml_files/benchmarks/lfr2/n1000/lfr_1000_mu3_s_0.gml");
            symMat = new SimilarityMatrix(g);
            int r = 0;

            while(r < runs) {
                Population p = new Population(g);
                p.generationActions();

//                p.printPop();
//                System.out.println(getArrayString(p.buildSolution()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getArrayString(int[] ar){
        String str = "";
        for(int i=0; i < ar.length; i++){

            str += ar[i];
            if (i != ar.length-1)
                str += ",";
        }
        return str;
    }

    /**
     * lazy method to write lists to sqlite
     * @param list
     */
    public static String listStrRep(List<Integer> list){
        String str = "";
        for(int i=0; i < list.size(); i++){
            str += list.get(i);
            if (i != list.size()-1)
                str += "\n";
        }
        return str;
    }
}
