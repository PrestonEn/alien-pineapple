import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MarginalExtractor {

    public static ArrayList<Integer> marginalNodes(Individual ind) {
        Set<Integer> vN = new HashSet<>();
        Set<Integer> vW = new HashSet<>();
        for (int i = 0; i < ind.locus.length; i++) {
            vN.add(i);
            vW.add(ind.locus[i]);
        }
        vN.removeAll(vW);
        return new ArrayList<>(vN);
    }

}
