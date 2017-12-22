package brock.pe12nh;

import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by preston on 2017-12-01.
 */
public class Population {
    ArrayList<Partition> pop;
    AdjGraph g;

    public Population(AdjGraph g){
        this.g = g;
        pop = new ArrayList<>();
        Iterator<Node> nodes = g.g.getNodeIterator();
        while(nodes.hasNext()){
            pop.add(new Partition(g,Integer.parseInt(nodes.next().getId())));
        }
    }

    public Partition selectParent(){
        return null;
    }
}
