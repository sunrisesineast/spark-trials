package com.example.sparktrials;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.ExperimentComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * This is an Array Adapter for the ListViews that show experiment information. Those ListViews are
 * on SearchFragment and both tabs of HomeFragment of MainActivity.
 */
public class CustomList extends ArrayAdapter<Experiment> {

    private ArrayList<Experiment> experimentsList;  // This is the list of experiments that the
                                                   // adapter will display on the ListView
    private Context context;

    // These are the TextViews in list_content.xml that will show the information about an
    // experiment
    private TextView experimentTitle;
    private TextView experimentDescription;
    private TextView experimentStatus;
    private TextView experimentOwner;
    private TextView experimentDate;
    private ImageView ownerIcon;

    /**
     * Constructor for a CustomList list adapter, which shows a customized list of experiments,
     * with some of their data
     * @param context
     *      The context which we are currently in
     * @param experiments
     *      The list of experiments which we want to display
     */
    public CustomList(@NonNull Context context, ArrayList<Experiment> experiments) {
        super(context, 0, experiments);
        this.context = context;
        experimentsList = experiments;
        experimentsList.sort(new ExperimentComparator()); // Sort based on descending order of date
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            // Set the layout of list items to be based on list_content.xml
            view = LayoutInflater.from(context).inflate(R.layout.list_content, parent, false);
        }

        experimentTitle = view.findViewById(R.id.list_experiment_title);
        experimentDescription = view.findViewById(R.id.list_experiment_description);
        experimentStatus = view.findViewById(R.id.list_experiment_status);
        experimentOwner = view.findViewById(R.id.list_experiment_owner);
        experimentDate = view.findViewById(R.id.list_experiment_date);
        ownerIcon = view.findViewById(R.id.owner_icon);

        setFields(position);

        // Launches a ProfileActivity when the username of an Experiment's Owner is clicked on
        experimentOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Experiment experiment = experimentsList.get(position);

                startProfileActivity(experiment.getOwner().getId());
            }
        });

        // Launches a ProfileActivity when the username of an Experiment's Owner is clicked on
        ownerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Experiment experiment = experimentsList.get(position);

                startProfileActivity(experiment.getOwner().getId());
            }
        });

        return view;
    }

    /**
     * This method sets the fields of an Experiment in the list to the data of the corresponding
     * experiment.
     * @param index
     *      This is the index of the Experiment whose information is to be displayed
     */
    private void setFields(int index) {
        Experiment experiment = experimentsList.get(index); // Get the respective experiment

        experimentTitle.setText(experiment.getTitle());
        experimentDescription.setText(experiment.getDesc());
        if (experiment.getOpen()) {
            experimentStatus.setText("Active");
            experimentStatus.setTextColor(ContextCompat.getColor(context, R.color.positive));
        } else {
            experimentStatus.setText("Inactive");
            experimentStatus.setTextColor(ContextCompat.getColor(context, R.color.neutral));
        }
        experimentOwner.setText(experiment.getOwner().getUsername());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        experimentDate.setText(dateFormat.format(experiment.getDate()));
    }

    /**
     * This method starts a ProfileActivity, which displays the information of the owner of an
     * experiment.
     * @param ownerId
     *      The user whose profile we want to display.
     */
    private void startProfileActivity(String ownerId) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("USER_ID", ownerId);
        ((Activity) getContext()).startActivityForResult(intent, 0); // Throwaway requestCode
    }
}
