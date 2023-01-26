package com.example.sparktrials.exp.admin;

/**
 * This class handles database updates and model changes for the admin fragment
 */

import androidx.lifecycle.ViewModel;

import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminViewModel extends ViewModel {

    FirebaseManager dbManager = new FirebaseManager();

    /**
     * Public Constructor for the AdminViewModel, currently creates it's own trial data for testing
     */
    public AdminViewModel() {

    }

    /**
     * Returns the list of users who have uploaded trials to this experiment
     * @return
     *  The list of users
     */
    public ArrayList<Profile> getUserList(ArrayList<Trial> trialList){
        ArrayList<Profile> userList = new ArrayList<>();
        for(Trial trial: trialList){
            if(userList.contains(trial.getProfile())){
                continue;
            } else {
                userList.add(trial.getProfile());
            }
        }
        return userList;
    }

    /**
     * Toggles the open attribute of the experiment in question
     * @param exp
     *  The Experiment to be opened or closed
     */
    public void toggleExpOpen(Experiment exp){
        Map<String, Object> map = new HashMap<>();
        map.put("Open", !exp.getOpen());
        if (exp.getOpen())
            exp.setOpen(false);
        else
            exp.setOpen(true);
        dbManager.update("experiments", exp.getId(), map);
    }

    public void toggleExpPublished(Experiment exp) {
        Map<String, Object> map = new HashMap<>();
        map.put("Published", !exp.getPublished());
        if(exp.getPublished()){
            exp.setPublished(false);
        } else {
            exp.setPublished(true);
        }
        dbManager.update("experiments", exp.getId(), map);
    }

    /**
     * Unpublish i.e. delete an experiment and unsubscribe all users from that experiment
     * @param id
     *  The id of the experiment being deleted
     */
    public void unpublishExperiment(String id){
        dbManager.unsubscribeUsers(id);
    }

}
