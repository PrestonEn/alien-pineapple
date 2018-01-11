package com.brock.pe12nh.TasginGA;

import java.util.Comparator;

/**
 * Created by speng on 1/6/2018.
 */
public class IndividualComparator implements Comparator<Individual> {
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.score, o2.score);
    }
}