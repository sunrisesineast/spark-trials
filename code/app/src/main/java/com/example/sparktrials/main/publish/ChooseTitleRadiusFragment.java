package com.example.sparktrials.main.publish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sparktrials.R;
import com.example.sparktrials.models.GeoMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * This represents the Fragment (to be displayed in MapsActivity) that allows the user to set the
 * radius of their region.
 */
public class ChooseTitleRadiusFragment extends DialogFragment {

    private GeoMap map;

    private EditText regionTitleEditText;
    private EditText radiusEditText;
    private Spinner unitsDropDownMenu;

    /**
     * Constructor for ChooseRadiusFragment.
     * @param map
     *      The map that the region was selected on.
     */
    public ChooseTitleRadiusFragment(GeoMap map) {
        this.map = map;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_choose_title_radius, null);

        regionTitleEditText = view.findViewById(R.id.title_edit_text);
        radiusEditText = view.findViewById(R.id.radius_edit_text);
        unitsDropDownMenu = view.findViewById(R.id.unit_drop_down_menu);

        // If attributes already set, display them
        double regionRadius = map.getGeoLocation().getRadius();
        String regionTitle = map.getGeoLocation().getRegionTitle();
        if (regionRadius > 0) {
            // Radius is stored in metres, but the default selection is kilometres
            radiusEditText.setText(Double.toString(regionRadius / 1000));
        }
        regionTitleEditText.setText(regionTitle);

        String[] units = {"km", "m"};
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, units);
        unitsDropDownMenu.setAdapter(unitsAdapter);

        // Create the dialog to be displayed
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton("Confirm Radius", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String titleString = regionTitleEditText.getText().toString();
                map.getGeoLocation().setRegionTitle(titleString);
                map.setCenterMarkerTitle(titleString);

                String radiusString = radiusEditText.getText().toString().trim();

                if (!radiusString.equals("")) {
                    double radius = Double.parseDouble(radiusString);
                    if (unitsDropDownMenu.getSelectedItem().equals("km")) {
                        // If the radius is set in kilometers, convert it to meters
                        map.getGeoLocation().setRadius(1000 * radius);
                    } else {
                        map.getGeoLocation().setRadius(radius);
                    }
                    // Display the region circle on the map
                    LatLng center = new LatLng(map.getGeoLocation().getLat(), map.getGeoLocation().getLon());
                    map.displayCircle(center, map.getGeoLocation().getRadius());
                }
            }
        });
        builder.setNeutralButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.spark_text));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.neutral));
        return dialog;
    }
}