/**
 * Created by speng on 7/31/2017.
 */

import java.util.stream.IntStream;
public class CommunityScore {
    /**
     *
     * @param s
     * @param r
     * @return
     */
    public static double CommunityScore(Solution s, double r){
        double comscore = 0d;
        for(int[][] sg: s.buildSubmats()){
            double meanSum = 0d;
            int sumOnes = 0;
            // for row
            for (int i = 0; i < sg.length; i++) {
                int numOnes = IntStream.of(sg[i]).sum();
                sumOnes += numOnes;
                meanSum += Math.pow(((double)numOnes/(double)sg.length),r);
            }
            double powerMean = meanSum/sg.length;
            comscore += powerMean * sumOnes;
        }
        return comscore;
    }
}

