import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual> {
    public int compare(Individual o1, Individual o2) {
        if (o1.score > o2.score) {
            return 1;
        } else if (o1.score < o2.score) {
            return -1;
        } else {
            return 0;
        }
    }
}
