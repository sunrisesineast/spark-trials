package com.example.sparktrials.models;

import junit.framework.TestCase;

public class TrialMeasurementTest extends TestCase {

    TrialMeasurement trial;

    /**
     * Verifies that the measure retrieved is the same as the measure that the class holds
     */
    public void testGetMeasure() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialMeasurement("1", location, profile, 100.0);
        assertEquals("getMeasure does not work", 100.0, trial.getValue());
    }

    /**
     * Verifies that the new measure set in the class is the measure that the class actually holds
     * Assumes that getMeasure works
     */
    public void testSetMeasure() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialMeasurement("1", location, profile, 100.0);
        trial.setMeasure(396.45555);
        assertEquals("setMeasure does not work", 396.45555, trial.getValue());
    }
}