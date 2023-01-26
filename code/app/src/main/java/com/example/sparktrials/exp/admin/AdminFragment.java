package com.example.sparktrials.exp.admin;

/**
 * This class contains the view information for the admin tab of an experiment page
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sparktrials.MainActivity;
import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Trial;

import java.util.ArrayList;

public class AdminFragment extends Fragment {

    View view;
    private AuditLog userTrialListAdapter;
    private ListView userTrialListView;
    private AdminViewModel manager;

    private Button endButton;
    private Button unpublishButton;
    private TextView activeText;

    Experiment experiment;

    /**
     * Constructor for the Admin fragment
     * @param experiment
     *  The experiment this admin fragment is for
     */
    public AdminFragment(Experiment experiment){
        this.experiment = experiment;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin, container, false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        userTrialListView = getView().findViewById(R.id.user_trials_list);

        manager = new ViewModelProvider(this).get(AdminViewModel.class);

        ArrayList<Trial> trialList = experiment.getAllTrials();
        ArrayList<Profile> userList = manager.getUserList(trialList);

        userTrialListAdapter = new AuditLog(getContext(), experiment, trialList, userList);
        userTrialListView.setAdapter(userTrialListAdapter);

        endButton = getView().findViewById(R.id.end_experiment_button);
        if(experiment.getOpen()){
            endButton.setText("END EXPERIMENT");
        } else {
            endButton.setText("OPEN EXPERIMENT");
        }
        unpublishButton = getView().findViewById(R.id.unpublish_experiment_button);
        if(experiment.getPublished()){
            unpublishButton.setText("Unpublish Experiment");
        } else {
            unpublishButton.setText("Publish Experiment");
        }

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The same button is used for opening and closing experiments, toggleExpOpen() is
                // used to flip the open attribute of the experiment.
                if(!experiment.getPublished() && !experiment.getOpen()){
                    Toast.makeText(getContext(), "Cannot Open an unpublished experiment", Toast.LENGTH_SHORT).show();
                    return;
                }
                manager.toggleExpOpen(experiment);
                if (endButton.getText() == "END EXPERIMENT"){
                    endButton.setText("OPEN EXPERIMENT");
                    activeText.setText("Inactive");
                    activeText.setTextColor(getResources().getColor(R.color.neutral));
                } else {
                    endButton.setText("END EXPERIMENT");
                    activeText.setText("Active");
                    activeText.setTextColor(getResources().getColor(R.color.positive));
                }
            }
        });

        unpublishButton.setOnClickListener((v) -> {
            if(experiment.getPublished()){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure you want to unpublish this experiment?");
                builder.setMessage("All experimenters will be unsubscribed from it.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //unpublish experiment will unsubscribe all users from the experiment and
                        // switch both the open and published attributes of the experiment to false
                        manager.unpublishExperiment(experiment.getId());
                        if(experiment.getOpen()) {
                            manager.toggleExpOpen(experiment);
                        }
                        manager.toggleExpPublished(experiment);
                        unpublishButton.setText("Publish Experiment");
                        endButton.setText("Open Experiment");
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.spark_text));
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.neutral));
            } else {
                manager.toggleExpPublished(experiment);
                unpublishButton.setText("Unpublish Experiment");
            }
        });
    }

    /**
     *Set the text that tells a user whether or not the experiment is active
     * @param activeText
     *  The textview that shows whether or not the experiment is active
     */
    public void setActiveText( TextView activeText){
        this.activeText = activeText;
    }
}