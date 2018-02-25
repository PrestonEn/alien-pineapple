import com.brock.pe12nh.AdjGraph.AdjGraph;
import org.graphstream.graph.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by speng on 1/15/2018.
 */
public class Test {
    public static void main(String args[]) throws IOException {
        Main.randgen = new Random();
        AdjGraph adjG = new AdjGraph("D:\\alien-pineapple\\benchmark_gen\\gml_files\\real_networks\\karate.gml");
        Iterator<Node> nn = adjG.g.getNode(0).getNeighborNodeIterator();
        while(nn.hasNext()){
            System.out.println(nn.next().getIndex());
        }

    }
}
