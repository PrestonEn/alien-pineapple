package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.ArrayList;

/**
 * Created by preston on 2017-12-01.
 */
public class Population {
    ArrayList<Partition> pop;
    AdjGraph g;

    public Population(AdjGraph g){
        this.g = g;
        pop = new ArrayList<>();

        g.g.getNodeIterator().forEachRemaining(n -> pop.add());
    }
}
