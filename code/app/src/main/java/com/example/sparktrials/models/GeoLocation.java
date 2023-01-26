package com.example.sparktrials.models;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * This is a class that contains information to record a point on the surface of the Earth
 * Lat is the latitude of the point
 * Lon is the longitude of the point
 */
public class GeoLocation {
    private double lat;
    private double lon;

    // If the radius is 0, then this just a point on the map, otherwise it is a region
    private double radius;

    // Some regions have titles
    private String regionTitle;


    public GeoLocation(){
        this.lat = 1000.0;
        this.lon = 1000.0;
        this.radius = 0;
        this.regionTitle = "";
    }

    /**
     * Constructor that initiates the point on Earth
     * Ensures that coordinates are valid
     * @param lat
     * @param lon
     */
    public GeoLocation(Double lat, Double lon){
        if (lat<-90){
            this.lat = -90.0;
        } else if (lat>90){
            this.lat = 90.0;
        } else {
            this.lat = lat;
        }

        if (lon<-180){
            this.lon = -180.0;
        } else if (lon>180){
            this.lon = 180.0;
        } else {
            this.lon = lon;
        }
        this.radius = 0;
        this.regionTitle = "";
    }

    public GeoLocation(Double lat, Double lon, Double radius, String regionTitle) {
        this(lat, lon);
        this.radius = radius;
        this.regionTitle = regionTitle;
    }

    /**
     * This method returns the lat/lon as a list of "coords"
     * @return
     *    Returns an array list containing the lat lon as a coord
     */
    public ArrayList<Double> getCoords(){
        ArrayList<Double> coords = new ArrayList<>();
        coords.add(this.lat);
        coords.add(this.lon);
        
        return coords;
    }

    /**
     *This returns the latitude of the point on Earth
     * @return
     *    Returns the latitude
     */
    public Double getLat() {
        return lat;
    }

    /**
     * This edits the latitude of this geolocation. Adjusts for invalid entry
     * @param lat
     *    Overrides the previously held lat value
     */
    public void setLat(Double lat) {
        if (lat<-90){
            this.lat = -90.0;
        } else if (lat>90){
            this.lat = 90.0;
        } else {
            this.lat = lat;
        }
    }

    /**
     * This returns the longitude of the point on Earth
     * @return
     *    Returns the longitude
     */
    public Double getLon() {
        return lon;
    }

    /**
     * This edits the latitude of this geolocation. Adjusts for invalid entry
     * @param lon
     *     Overrides the previously held lon value
     */
    public void setLon(Double lon) {
        if (lon<-180){
            this.lon = -180.0;
        } else if (lon>180){
            this.lon = 180.0;
        } else {
            this.lon = lon;
        }
    }

    /**
     * This returns the radius of a region
     * @return
     *     Returns the radius of the region
     */
    public Double getRadius() {
        return this.radius;
    }

    /**
     * This edits the radius of this geolocation. Adjusts for invalid entry
     * @param radius
     *     Overrides the previously held radius value
     */
    public void setRadius(double radius) {
        if (radius < 0) {
            this.radius = 0;
        } else {
            this.radius = radius;
        }
    }

    /**
     * This returns the title of a region
     * @return
     *     Returns the title of the region
     */
    public String getRegionTitle() {
        return this.regionTitle;
    }

    /**
     * This edits the title of this geolocation
     * @param regionTitle
     *     Overrides the previously held title value
     */
    public void setRegionTitle(String regionTitle) {
        this.regionTitle = regionTitle;
    }
}
