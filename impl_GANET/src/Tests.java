/**
 * Created by preston on 2017-08-22.
 */
public class Tests {

    public static void main(String args[]){
        int[] locus = {1,0,1,2,5,4,5,9,7,10,7};
        Individual i = new Individual(locus, null);
        System.out.println(i.getMembershipString());
    }
}
