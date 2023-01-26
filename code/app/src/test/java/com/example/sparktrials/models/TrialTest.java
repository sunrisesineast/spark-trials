package com.example.sparktrials.models;

import junit.framework.TestCase;

/**
 * A class to test the Trial class
 * Tests every method of the Trial class using a subclass TrialCount
 */
public class TrialTest extends TestCase {
    Trial trial;

    /**
     * Tests getId by verifying that the retrieved id is the id that the trial holds
     */
    public void testGetId() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo", "foo", "foo");
        this.trial = new TrialCount("21598721", geoLocation, profile);
        assertEquals("getId not working", "21598721", trial.getId());
    }

    /**
     * Tests setId by verifying that the new id is set in the Trial class
     * Also ensures that no overwrites are allowed
     */
    public void testSetId() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo", "foo", "foo");
        this.trial = new TrialCount(null, geoLocation, profile);
        trial.setId("395210");
        assertEquals("setId not working", "395210", trial.getId());
        trial.setId("5");
        assertEquals("setId allows overwrites", "395210", trial.getId());
    }

    /**
     * Tests get location by verifying that the retrieved location is the location that the trial holds
     * Does this by comparing each attribute of the locations
     */
    public void testGetLocation() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo", "foo", "foo");
        this.trial = new TrialCount("5", geoLocation, profile);
        assertEquals("getLocation not working", geoLocation.getLat(), trial.getLocation().getLat());
        assertEquals("getLocation not working", geoLocation.getLon(), trial.getLocation().getLon());
    }

    /**
     * Tests set location by verifying that the new location is set in the Trial class
     */
    public void testSetLocation() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo", "foo", "foo");
        this.trial = new TrialCount("5", geoLocation, profile);
        GeoLocation geoLocation2 = new GeoLocation(45.6, -112.4);
        trial.setLocation(geoLocation2);
        assertEquals("getLocation not working", geoLocation2.getLat(), trial.getLocation().getLat());
        assertEquals("getLocation not working", geoLocation2.getLon(), trial.getLocation().getLon());
    }

    /**
     * Tests get profile by verifying that the retrieved profile is the profile that the trial holds
     * Does this by comparing every attribute of profile
     */
    public void testGetProfile() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        this.trial = new TrialCount("5", geoLocation, profile);
        assertEquals("getProfile not working", profile.getId(), trial.getProfile().getId());
        assertEquals("getProfile not working", profile.getContact(), trial.getProfile().getContact());
        assertEquals("getProfile not working", profile.getUsername(), trial.getProfile().getUsername());
    }

    /**
     * Tests set profile by verifying that the new profile is the profile that the Trial class holds
     * Ensures that no overwrites are allowed
     */
    public void testSetProfile() {
        this.trial = new TrialCount();
        Profile profile1 = new Profile("foo1", "foo2", "foo3");
        Profile profile2 = new Profile("foobar1", "foobar2", "foobar3");
        this.trial.setProfile(profile1);
        assertEquals("getProfile not working", profile1.getId(), trial.getProfile().getId());
        assertEquals("getProfile not working", profile1.getContact(), trial.getProfile().getContact());
        assertEquals("getProfile not working", profile1.getUsername(), trial.getProfile().getUsername());
        this.trial.setProfile(profile2);
        assertEquals("getProfile allows overwrites", profile1.getId(), trial.getProfile().getId());
        assertEquals("getProfile allows overwrites", profile1.getContact(), trial.getProfile().getContact());
        assertEquals("getProfile allows overwrites", profile1.getUsername(), trial.getProfile().getUsername());
    }

    /**
     * Tests overwrite profile by verifying that the new profile is the profile that the Trial class holds
     */
    public void testOverwriteProfile() {
        GeoLocation geoLocation = new GeoLocation(30.0, 50.0);
        Profile profile = new Profile("foo1", "foo2", "foo3");
        Profile profile2 = new Profile("foobar1", "foobar2", "foobar3");
        this.trial = new TrialCount("5", geoLocation, profile);
        this.trial.overwriteProfile(profile2);
        assertEquals("overwriteProfile not working", profile2.getId(), trial.getProfile().getId());
        assertEquals("overwriteProfile not working", profile2.getContact(), trial.getProfile().getContact());
        assertEquals("overwriteProfile not working", profile2.getUsername(), trial.getProfile().getUsername());
    }
}