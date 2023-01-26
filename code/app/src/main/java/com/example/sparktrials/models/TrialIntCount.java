package com.example.sparktrials.models;

/**
 * This class extends the basic trial class. This type of trial has a set count
 */
public class TrialIntCount extends Trial {

    /**
     * Initializes a blank trial with a set count
     * @param count
     *    The count that this trial will hold
     */
    public TrialIntCount(Integer count){
        super();
        if (count>=0.0) {
            this.value = (double)count;
        } else {
            this.value = 0.0;
        }
    }
    /**
     * This constructor initializes a filled trial with a given count
     * @param id
     *    The id of the trial. Generated elsewhere and passed in
     * @param location
     *    The location that the trial was held. Will often be null
     * @param profile
     *    The profile of the user that created the trial
     * @param count
     *    Non-negative integer
     */
    public TrialIntCount(String id, GeoLocation location, Profile profile, Integer count){
        super(id, location, profile);
        if (count>=0.0) {
            this.value = (double)count;
        } else {
            this.value = 0.0;
        }
    }


    /**
     * Updates the count of this trial
     * @param count
     *    The new count that the count attribute will hold
     */
    public void setCount(Integer count) {
        if (count>0){
            this.value = (double)count;
        }
    }
}
