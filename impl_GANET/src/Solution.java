import com.brock.pe12nh.AdjGraph.AdjGraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Solution {
    public int[] membership;
    public HashMap<Integer, ArrayList<Integer>> groups;
    private AdjGraph g;

    public Solution(int[] decoded, AdjGraph g){
        this.membership = decoded;
        this.groups = new HashMap<>();
        createGroups();
        this.g = g;
    }

    private void createGroups(){
        for(int i=0; i<membership.length; i++){
            if(!groups.containsKey(membership[i])){
                groups.put(membership[i], new ArrayList<>());
                groups.get(membership[i]).add(i);
            }else {
                groups.get(membership[i]).add(i);
            }
        }
    }

    public ArrayList<int[][]> buildSubmats(){
        ArrayList<int[][]> subMats = new ArrayList<>();
        for (ArrayList<Integer> al:
             groups.values()) {
            int[][] sm = new int[al.size()][al.size()];
            for (int i=0; i<al.size(); i++){
                for (int j=0; j<al.size(); j++){
                    sm[i][j]=g.adjMat[al.get(i)][al.get(j)]?1:0;
                }
            }
            subMats.add(sm);
        }
        return subMats;
    }

}
