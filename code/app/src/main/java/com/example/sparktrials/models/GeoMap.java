package com.example.sparktrials.models;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * This class represents a GoogleMaps API and is responsible for displaying any Google Maps screem
 * in the application.
 */
public class GeoMap implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker centerMarker;
    private MarkerOptions centerMarkerOptions;
    private Circle circle;

    private GeoLocation geoLocation;
    private ArrayList<Trial> trials;

    private boolean isEditable;
    private boolean markerSet;
    private boolean hasLocationSet;

    /**
     * Constructor of GeoMap that is called when we want to set a region to an experiment.
     * @param isEditable
     *      This indicates whether the map is "editable", i.e, you can change the location of the
     *      marker.
     */
    public GeoMap(boolean isEditable) {
        this.isEditable = isEditable;
        geoLocation = new GeoLocation();
        markerSet = false;
        hasLocationSet = false;
    }

    /**
     * Constructor of GeoMap that is called when the Map tab of ExperimentActivity is clicked on.
     * @param isEditable
     *      This indicates whether the map is "editable", i.e, you can change the location of the
     *      marker.
     */
    public GeoMap(Experiment experiment, boolean isEditable) {
        this.isEditable = isEditable;
        this.geoLocation = experiment.getRegion();
        markerSet = true;
        hasLocationSet = experiment.hasLocationSet();
        if (hasLocationSet) {
            trials = experiment.getValidTrials();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (isEditable) { // i.e. if the user is picking a location (in MapsActivity)

            // The map will show will Canada when first launched. I got the following
            // values after a simple google search.
            LatLng edmontonCityCentre = new LatLng(53.5439, -113.4923);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmontonCityCentre, 3.0f));

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng center) {
                    // Clear any previous markers
                    map.clear();

                    geoLocation.setLat(center.latitude);
                    geoLocation.setLon(center.longitude);

                    setCenterMarkerOptions(center);

                    // Add a marker to the map
                    centerMarker = map.addMarker(centerMarkerOptions);

                    // Center the screen around the marker
                    map.moveCamera(CameraUpdateFactory.newLatLng(center));

                    markerSet = true;
                }
            });
        } else if (hasLocationSet) { // If the user is looking at the MAP tab of a
                                     // location-enabled experiment

            map.clear(); // Clear the map of anything

            LatLng center = new LatLng(geoLocation.getLat(), geoLocation.getLon());
            setCenterMarkerOptions(center);

            // Add a marker to the center of the region, and set its title
            centerMarker = map.addMarker(centerMarkerOptions);
            setCenterMarkerTitle(geoLocation.getRegionTitle());

             // Add an orange marker for every trial
             for (Trial trial : trials) {
                 double trialLat = trial.getLocation().getLat();
                 double trialLon = trial.getLocation().getLon();
                 LatLng trialLocation = new LatLng(trialLat, trialLon);

                 map.addMarker(new MarkerOptions()
                         .position(trialLocation)
                         .title(trial.getProfile().getUsername())
                         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
             }

             // Display a circle centered around the region center, marking the region
             displayCircle(center, geoLocation.getRadius());

             float zoomLevel = 13.0f;
             map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));

        }
    }

    /**
     * Gets the geolocation that represents the region of the experiment.
     * @return
     *      Returns the geolocation of the experiment.
     */
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * Gets whether a marker is already set on the map or not.
     * @return
     *      Returns true if a marker is set on the map, false otherwise.
     */
    public boolean isMarkerSet() {
        return markerSet;
    }

    /**
     * Displays a circle on the map. Meant to display a circle centered around the marker of the
     * region set by the user.
     * @param center
     *      This represents the center of the circle.
     * @param radius
     *      This represents the radius of the circle in meters.
     */
    public void displayCircle(LatLng center, double radius) {
        if (circle != null) {
            circle.remove();
        }

        // Set the attributes of the circle to be drawn on the map.
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(center);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(5);
        circleOptions.strokeColor(Color.BLUE);
        circleOptions.fillColor(Color.argb(50, 0, 0, 120));

        // Add a circle to the map with the attributes set above.
        circle = map.addCircle(circleOptions);
    }

    /**
     * Sets a title to the marker of the region's center.
     * @param title
     *      The title to be set
     */
    public void setCenterMarkerTitle(String title) {
        centerMarker.setTitle(title);
        centerMarker.showInfoWindow();
    }

    /**
     * Creates and sets a (red) marker marking the center of the region.
     * @param center
     *      The coordinates of the marker to be set, i.e. the coordinates of the region center
     */
    private void setCenterMarkerOptions(LatLng center) {
        centerMarkerOptions = new MarkerOptions()
                                .position(center);
    }
}
