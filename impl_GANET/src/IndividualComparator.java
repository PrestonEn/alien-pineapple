import java.util.Comparator;

/**
 * Created by speng on 1/4/2018.
 */
public class IndividualComparator implements Comparator<Individual> {
    public int compare(Individual o1, Individual o2) {
        if (o1.score > o2.score) {
            return 1;
        } else if (o1.score < o2.score) {
            return -1;
        }else {
            return 0;
        }
        }
}
