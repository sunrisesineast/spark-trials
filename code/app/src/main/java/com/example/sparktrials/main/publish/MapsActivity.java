package com.example.sparktrials.main.publish;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.example.sparktrials.MainActivity;
import com.example.sparktrials.R;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.GeoMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * This class represents an activity that allows the user to pick a region for an experiment before
 * publishing it. It is meant to be called from PublishExperimentFragment.
 */
public class MapsActivity extends AppCompatActivity {

    private GeoMap map;

    private Button confirmLocation;
    private Button chooseRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        displayUpButton();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_map_activity);

        map = new GeoMap(true);

        // Display map when it loads
        mapFragment.getMapAsync(map);

        confirmLocation = findViewById(R.id.confirm_region_button);
        chooseRadius = findViewById(R.id.choose_radius_button);

        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.isMarkerSet()) {
                    if (map.getGeoLocation().getRadius() > 0) {
                        sendBackCoords(true);
                    } else {
                        // If radius is not set, ask for radius again
                        new ChooseTitleRadiusFragment(map).show(getSupportFragmentManager(), "Choose Region Fragment");
                    }
                } else {
                    // Region cannot be selected if there is no center (and no radius)
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("ERROR")
                            .setMessage("Long click somewhere to set a center for your region.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });

        chooseRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.isMarkerSet()) {
                    new ChooseTitleRadiusFragment(map).show(getSupportFragmentManager(), "Choose Region Fragment");
                } else {
                    // Radius for the region cannot be set before the center for the region is chosen
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("ERROR")
                            .setMessage("Long click somewhere to set a center for your region.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });
    }

    /**
     * Displays the "Up" button in the toolbar of the activity.
     */
    private void displayUpButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_activity_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Sets the "Up" button to act as if a the "Back" button was pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // If the "Back" button is pressed, then no region has been selected.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBackCoords(false);
    }

    /**
     * Sends back the coordinates of the center and the radius of the region back to the UI
     * element that called it.
     * @param hasPickedLocation
     *      Indicates whether a region has been chosen.
     */
    private void sendBackCoords(boolean hasPickedLocation) {
        // Get the result codes
        int didPickLocation = (int) getIntent().getExtras().get("LOCATION_PICKED");
        int didNotPickLocation = (int) getIntent().getExtras().get("NO_LOCATION_PICKED");

        if (hasPickedLocation) {
            Intent sendLocationBack = new Intent(this, MainActivity.class);
            GeoLocation markedLocation = map.getGeoLocation();
            sendLocationBack.putExtra("Latitude", markedLocation.getLat());
            sendLocationBack.putExtra("Longitude", markedLocation.getLon());
            sendLocationBack.putExtra("Radius", map.getGeoLocation().getRadius());
            sendLocationBack.putExtra("Region Title", map.getGeoLocation().getRegionTitle());

            setResult(didPickLocation, sendLocationBack);
        } else {
            setResult(didNotPickLocation);
        }
        finish();
    }

}