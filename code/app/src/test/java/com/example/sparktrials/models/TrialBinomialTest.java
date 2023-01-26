package com.example.sparktrials.models;

import junit.framework.TestCase;

/**
 * Tests only the subclass methods for the TrialBinomial subclass
 */
public class TrialBinomialTest extends TestCase {

    TrialBinomial trial;

    /**
     * Verifies that pass value retrieved is the pass value that the class should have
     */
    public void testGetPass() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialBinomial("1", location, profile, true);
        assertEquals("getPass does not work", 1, (int)trial.getValue());
    }

    /**
     * Verifies that new pass value that is being set is actually held by the class
     */
    public void testSetPass() {
        GeoLocation location = new GeoLocation(30.0, 40.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialBinomial("1", location, profile, true);
        this.trial.setPass(false);
        assertEquals("setPass does not work", 0, (int)trial.getValue());
    }
}