package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Individual {
    List<Integer> genes;
    AdjGraph g;

    public Individual(List<Integer> vals, AdjGraph g){
        genes = vals;
        this.g = g;
    }


}
