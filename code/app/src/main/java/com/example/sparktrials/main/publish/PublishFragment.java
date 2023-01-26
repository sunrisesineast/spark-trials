package com.example.sparktrials.main.publish;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sparktrials.IdManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;

/**
 * The class containing the UI associated with publishing experiments, handles input and UI
 */
public class PublishFragment extends DialogFragment {
    private EditText expDesc;
    private EditText expTitle;
    private EditText expMinNTrials;
    private Spinner spinner;
    private Spinner locationSet;
    private TextView reqTrialLocations;
    private Spinner trialLocations;

    private double lat;
    private double lon;
    private double radius;
    private String regionTitle;

    final private int didNotPickLocation = 0;
    final private int didPickLocation = 1;

    /**
     * onCreate Dialog which prompts the user to enter a title, description, minimum number of trials and a lat long pair.
     * Then calls PublishFragmentManager which uploads the experiment to firestore
     * @param savedInstanceState
     * @return
     */
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        if (!hasInternetConnectivity()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("No Internet Connection");
            builder.setMessage("Publishing Experiments not Available");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.neutral));
            return alert;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_publish, null);
        expDesc = view.findViewById(R.id.expDesc_editText);
        expTitle = view.findViewById(R.id.expTitle_editText);
        expMinNTrials = view.findViewById(R.id.expMinNTrials_editText);
        spinner = view.findViewById(R.id.experiment_type_spinner);
        locationSet = view.findViewById(R.id.experiment_location_spinner);
        reqTrialLocations = view.findViewById(R.id.request_trials_location);
        trialLocations = view.findViewById(R.id.trial_location_spinner);
        String[] items = new String[]{"Binomial Trials", "Counts", "Non-Negative Integer Counts","Measurement Trials"};
        String[] locationOptions = new String[]{"False", "True"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,locationOptions);
        spinner.setAdapter(adapter);
        locationSet.setAdapter(adapter2);
        trialLocations.setAdapter(adapter2);



        locationSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Boolean chooseLocation = Boolean.parseBoolean(locationSet.getSelectedItem().toString());
                if (chooseLocation) {
                    startMapsActivity();
                    reqTrialLocations.setVisibility(View.VISIBLE);
                    trialLocations.setVisibility(View.VISIBLE);
                } else {
                    reqTrialLocations.setVisibility(View.INVISIBLE);
                    trialLocations.setVisibility(View.INVISIBLE);
                    trialLocations.setSelection(0); // Set to False
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        IdManager idManager = new IdManager(getActivity());
        String id = idManager.getUserId();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setView(view)
                .setNeutralButton("X", null)
                .setPositiveButton("POST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String desc = expDesc.getText().toString();
                        String title = expTitle.getText().toString();
                        String MinNTrialsString = expMinNTrials.getText().toString();
                        String experimentType = spinner.getSelectedItem().toString();
                        Boolean reqLocation = Boolean.parseBoolean(trialLocations.getSelectedItem().toString());
                        Log.d("Type",experimentType);
                        PublishFragmentManager manager = new PublishFragmentManager(id,desc,title,MinNTrialsString,lat,lon,radius,regionTitle,experimentType,reqLocation);
                    }
                })
                .create();

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.spark_text));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.neutral));
        return dialog;
    }

    /**
     * Starts MapsActivity which allows the user to pick a location
     */
    private void startMapsActivity() {
        Intent launchMapsActivity = new Intent(getContext(), MapsActivity.class);

        // Attach the result codes
        launchMapsActivity.putExtra("NO_LOCATION_PICKED", didNotPickLocation);
        launchMapsActivity.putExtra("LOCATION_PICKED", didPickLocation);

        startActivityForResult(launchMapsActivity, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == didNotPickLocation) {
            // Set Location field to False
            locationSet.setSelection(0);
        } else if (resultCode == didPickLocation) {
            lat = (double) data.getExtras().get("Latitude");
            lon = (double) data.getExtras().get("Longitude");
            radius = (double) data.getExtras().get("Radius");
            regionTitle = (String) data.getExtras().get("Region Title");
        }
    }

    /**
     * Checks if the device is connected to the internet
     * @return
     *      Returns true if device is connected tp the internet, false otherwise
     */
    public boolean hasInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

}
