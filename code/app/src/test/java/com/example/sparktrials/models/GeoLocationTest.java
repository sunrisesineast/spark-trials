package com.example.sparktrials.models;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Tests the GeoLocation class, per function
 */
public class GeoLocationTest extends TestCase {

    GeoLocation location;

    /**
     * Tests the get coords function by seeing if the coords list returned
     * has the right size, and the right elements
     */
    public void testGetCoords() {
        this.location = new GeoLocation(55.3, 89.06);
        ArrayList<Double> coords = location.getCoords();
        assertEquals("Too many numbers in coords", 2, coords.size());
        assertEquals("Incorrect lat from getCoords", 55.3, coords.get(0));
        assertEquals("Incorrect lon from getCoords", 89.06, coords.get(1));
    }

    /**
     * Tests the get lat function by checking if the returned value is what was inputted
     */
    public void testGetLat() {
        this.location = new GeoLocation(-13.0696, 112.05);
        assertEquals("Incorrect lat from getLat", -13.0696, location.getLat());
    }

    /**
     * Tests the set lat function by checking if it works on regular lats, and invalid lats
     * Assumes that the getLat method works
     */
    public void testSetLat() {
        this.location = new GeoLocation(-13.0696, 112.05);
        location.setLat(12.1);
        assertEquals("Lat setter not working", 12.1, location.getLat());
        location.setLat(-38.3);
        assertEquals("Lat setter not working with negative entries", -38.3, location.getLat());
        location.setLat(-100.0);
        assertEquals("Lat setter doesn't maintain validity", -90.0, location.getLat());
        location.setLat(100.0);
        assertEquals("Lat setter doesn't maintain validity", 90.0, location.getLat());
    }

    /**
     * Tests the get lon function by checking if the returned value is what was inputted
     */
    public void testGetLon() {
        this.location = new GeoLocation(-13.0696, 112.05);
        assertEquals("Incorrect lon from getLon", 112.05, location.getLon());
    }

    /**
     * Tests the set lon function by checking if it works on regular lons and invalid lons
     * Assumes that the getLon method works
     */
    public void testSetLon() {
        this.location = new GeoLocation(-13.0696, 112.05);
        location.setLon(12.1);
        assertEquals("Lon setter not working", 12.1, location.getLon());
        location.setLon(-38.3);
        assertEquals("Lon setter not working with negative entries", -38.3, location.getLon());
        location.setLon(-200.0);
        assertEquals("Lon setter doesn't maintain validity", -180.0, location.getLon());
        location.setLon(200.0);
        assertEquals("Lon setter doesn't maintain validity", 180.0, location.getLon());
    }
}