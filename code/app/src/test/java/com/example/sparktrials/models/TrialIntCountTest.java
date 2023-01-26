package com.example.sparktrials.models;

import junit.framework.TestCase;

/**
 * Test for only the subclass methods of TrialIntCount
 */
public class TrialIntCountTest extends TestCase {
    TrialIntCount trial;

    /**
     * Verifies that the getCount method retrieves the count that the class actually holds
     */
    public void testGetCount() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialIntCount("1", location, profile, 55);
        assertEquals("getCount does not work", 55, (int)trial.getValue());
    }

    /**
     * After updating the count value, checks if the class actually holds this updated value
     * Assumes that getCount works
     */
    public void testSetCount() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialIntCount("1", location, profile, 55);
        trial.setCount(3980);
        assertEquals("setCount does not work", 3980, (int)trial.getValue());
    }
}