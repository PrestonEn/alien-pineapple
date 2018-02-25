import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 326034519 on 2017/08/09.
 */
public class Solution {
    public int[] membership;
    public HashMap<Integer, ArrayList<Integer>> groups;
    private AdjGraph g;

    public Solution(int[] decoded, AdjGraph g) {
        this.membership = decoded;
        this.groups = new HashMap<>();
        createGroups();
        this.g = g;
    }

    private void createGroups() {
        for (int i = 0; i < membership.length; i++) {
            if (!groups.containsKey(membership[i])) {
                groups.put(membership[i], new ArrayList<Integer>());
                groups.get(membership[i]).add(i);
            } else {
                groups.get(membership[i]).add(i);
            }
        }
    }

}
