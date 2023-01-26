package com.example.sparktrials.exp.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * This class represents a Fragment that is meant to display a map that shows the region of an
 * experiment and the locations of its trials.
 */
public class LocationFragment extends Fragment{
    View view;
    SupportMapFragment mapFragment;

    Experiment experiment;
    GeoMap map;

    /**
     * Constructor for LocationFragment
     * @param experiment
     *      This is the experiment whose region and locations of trials we want to display ont the
     *      map.
     */
    public LocationFragment(Experiment experiment){
        this.experiment = experiment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);

        // This is the fragment that will display the map
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_fragment_experiment_activity);

        // A map shown under the "Map" tab of ExperimentActivity is not editable.
        map = new GeoMap(experiment, false);

        // Display the map once it loads
        mapFragment.getMapAsync(map);

        return view;
    }

}
