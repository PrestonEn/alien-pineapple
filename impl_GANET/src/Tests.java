import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.io.IOException;

public class Tests {

    public static void main(String args[]) throws IOException {
        AdjGraph g = new AdjGraph("D:/alien-pineapple/benchmark_gen/gml_files/benchmarks/gn/girvan_2.gml");
        int[] members = {0,1,0,2,2,1,3,1,1,0,3,3,0,2,1,3,2,1,3,3,2,0,1,2,1,1,3,0,0,3,2,1,0,2,3,0,0,1,2,0,3,0,3,3,1,1,3,1,3,2,3,3,3,3,0,2,2,2,0,0,2,3,0,1,2,2,0,1,1,0,0,2,1,1,1,2,3,1,2,1,1,2,3,0,3,2,0,0,1,3,1,3,2,0,3,2,2,2,3,2,0,1,3,3,2,0,1,3,1,0,0,1,3,2,1,3,3,2,1,1,2,0,0,2,0,0,0,2};
        Solution s = new Solution(members, g);
        System.out.println(CommunityScore.CommunityScore(s, 0.9));

    }
}
