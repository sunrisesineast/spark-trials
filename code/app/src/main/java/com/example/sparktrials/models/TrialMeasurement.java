package com.example.sparktrials.models;

/**
 * This class extends the basic trial class. This type of trial will keep a double number
 * used to represent some kind of measurement
 */
public class TrialMeasurement extends Trial{

    /**
     * Initializes a blank trial and sets the measure attribute to measure
     * @param measure
     *    The measure that this trial will initially hold
     */
    public TrialMeasurement(Double measure){
        super();
        this.value = measure;
    }
    /**
     * This constructor initializes a filled trial with a given measurement
     * @param id
     *    The id of the trial. Generated elsewhere and passed in
     * @param location
     *    The location that the trial was held. Will often be null
     * @param profile
     *    The profile of the user that created the trial
     * @param measure
     *    A measure of something that the measure attribute will take
     */
    public TrialMeasurement(String id, GeoLocation location, Profile profile, Double measure){
        super(id, location, profile);
        this.value = measure;
    }

    /**
     * Updates the measure attribute
     * @param measure
     *    The new measure that this trial will hold
     */
    public void setMeasure(Double measure) {
        this.value = measure;
    }
}
