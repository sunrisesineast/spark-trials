package com.example.sparktrials.models;

import java.util.Comparator;

/**
 * Used to compare two experiments based on date.
 */
public class ExperimentComparator implements Comparator<Experiment> {
    @Override
    public int compare(Experiment o1, Experiment o2) {
        // o1 is 'less than' o2 if o1 was created after o2.
        return -1*o1.getDate().compareTo(o2.getDate());
    }
}
