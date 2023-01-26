package com.example.sparktrials;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;
import com.example.sparktrials.models.TrialCount;
import com.example.sparktrials.models.TrialIntCount;
import com.example.sparktrials.models.TrialMeasurement;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to manage the data for ExperimentActivity - experiment and profile
 */
public class ExperimentViewModel extends ViewModel {
    public FirebaseManager manager = new FirebaseManager();

    private MutableLiveData<Experiment> exp;
    private String expId;
    private MutableLiveData<Profile> pro;
    private String proId;

    final String TAG = "Fetching Experiment...";

    /**
     * Constructor for SearchViewModel
     */
    public ExperimentViewModel() {
        exp = new MutableLiveData<>();
        pro = new MutableLiveData<>();
    }

    /**
     * Initializes profile and experiment
     * @param id
     *    the id of the experiment to retrieve from firebase
     * @param uid
     *    the id of the profile to retrieve from firebase
     */
    public void init(String id, String uid){
        expId = id;
        proId = uid;
        downloadExperiment();
    }

    /**
     * Gets the experiments in the database
     * @return
     *      Returns the list of experiments in the database
     */
    public MutableLiveData<Experiment> getExperiment() {
        return exp;
    }

    /**
     * Gets profile in the database
     * @return
     *    Returns the profile in the database
     */
    public MutableLiveData<Profile> getProfile() {return pro;}

    /**
     * Is called when subscribe button is pressed
     * Adds or remove the experiment from subscription list of the profile
     * @return
     *    returns the profile, so that it updates the button name
     */
    public MutableLiveData<Profile> subscribe(){
        Profile temp = new Profile(proId);

        if (pro.getValue() != null) {
            if (pro.getValue().getSubscriptions().contains(exp.getValue().getId())) {
                //unsubscribe
                pro.getValue().delSubscription(exp.getValue().getId());
            } else {
                //subscribe
                pro.getValue().addSubscription(exp.getValue().getId());
            }

            temp.setSubscriptions(pro.getValue().getSubscriptions());
            temp.setUsername(pro.getValue().getUsername());
            temp.setContact(pro.getValue().getContact());
        }
        pro.setValue(temp);
        return pro;
    }

    /**
     * Uploads the subscription list to firebase
     */
    public void updateSubscribe(){
        if (pro.getValue() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("subscriptions", pro.getValue().getSubscriptions());

            manager.update("users", proId, map);
        }
    }

    /**
     * FIlls the pro attribute with a profile from firebase
     */
    private void downloadProfile() {
        manager.get("users", proId, proData -> {
            Profile profile = new Profile(proId);

            Log.d(TAG, proData.getId() + " => " + proData.getData());
            profile.setUsername((String) proData.getData().get("name"));
            profile.setContact((String) proData.getData().get("contact"));
            profile.setSubscriptions((ArrayList<String>) proData.getData().get("subscriptions"));
            Log.d(TAG, proData.getId() + " => " + proData.getData());

            pro.setValue(profile);

        });
    }

    /**
     * Gets an experiment from the database.
     */
    private void downloadExperiment() {
        manager.get("experiments", expId, expData -> {
            downloadProfile();
            Experiment experiment = new Experiment(expId);

            Log.d(TAG, expData.getId() + " => " + expData.getData());
            experiment.setTitle((String) expData.getData().get("Title"));
            experiment.setDesc((String) expData.getData().get("Description"));
            GeoLocation region = new GeoLocation();
            region.setLat((Double) expData.getData().get("Latitude"));
            region.setLon((Double) expData.getData().get("Longitude"));
            region.setRadius((Double) expData.getData().get("Radius"));
            region.setRegionTitle((String) expData.getData().get("Region Title"));
            experiment.setRegion(region);
            experiment.setReqLocation((Boolean) expData.getData().get("ReqLocation"));
            experiment.setMinNTrials(((Long) expData.getData().get("MinNTrials")).intValue());
            Timestamp date = (Timestamp) expData.getData().get("Date");
            experiment.setDate(date.toDate());
            experiment.setOpen((Boolean) expData.getData().get("Open"));
            experiment.setPublished((Boolean) expData.getData().get("Published"));
            experiment.setType((String) expData.getData().get("Type"));
            String uId = (String) expData.getData().get("profileID");
            Profile owner = new Profile(uId);
            experiment.setOwner(owner);
            ArrayList<HashMap> trialsHash = (ArrayList<HashMap>) expData.getData().get("Trials");
            ArrayList<Trial> trials = new ArrayList<>();
            for(HashMap<String, Object> map: trialsHash){
                Trial trial;
                String type = experiment.getType();
                if(type.equals("binomial trials")){
                    if((Double) map.get("value") == 0.0){
                        trial = new TrialBinomial(false);
                    } else {
                        trial = new TrialBinomial(true);
                    }

                } else if(type.equals("non-negative integer counts")) {
                    trial = new TrialIntCount(((Double) map.get("value")).intValue());
                } else if(type.equals("measurement trials")){
                    trial = new TrialMeasurement((Double) map.get("value"));
                } else {
                    trial = new TrialCount();
                }

                if (experiment.hasLocationSet()) {
                    HashMap<String, Object> trialCoords = (HashMap<String, Object>) map.get("location");
                    GeoLocation trialLocation = new GeoLocation();
                    trialLocation.setLat((double) trialCoords.get("lat"));
                    trialLocation.setLon((double) trialCoords.get("lon"));
                    trial.setLocation(trialLocation);
                }

                Profile experimenter = new Profile();
                HashMap<String, Object> profile = (HashMap<String, Object>) map.get("profile");
                experimenter.setContact((String) profile.get("contact"));
                experimenter.setUsername((String) profile.get("username"));
                experimenter.setId((String) profile.get("id"));
                experimenter.setSubscriptions((ArrayList<String>) profile.get("subscriptions"));
                trial.setProfile(experimenter);
                trial.setId((String) map.get("id"));
                Timestamp trialDate = (Timestamp) map.get("date");
                trial.setDate(trialDate.toDate());
                trials.add(trial);
            }
            experiment.setTrials(trials);
            experiment.setBlacklist((ArrayList<String>) expData.getData().get("Blacklist"));
            Log.d(TAG, expData.getId() + " => " + expData.getData());

            manager.get("users", experiment.getOwner().getId(), proData -> {
                Log.d(TAG, proData.getId() + " => " + proData.getData());
                experiment.getOwner().setUsername((String) proData.getData().get("name"));

                exp.setValue(experiment);
            });

        });


    }



}
