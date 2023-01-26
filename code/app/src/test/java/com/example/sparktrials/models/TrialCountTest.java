package com.example.sparktrials.models;

import junit.framework.TestCase;

/**
 * A class to test only the TrialCount subclass methods
 */
public class TrialCountTest extends TestCase {

    TrialCount trial;

    /**
     * Tests that the get and set functions both work by verifying that
     * we retrieve the correct count and after a change to count, we also retrieve
     * that correct count
     */
    public void testCreateCount() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialCount("1", location, profile);
        assertEquals("get/setCount does not work", 1, (int)trial.getValue());
    }

}