package com.example.sparktrials.main.publish;

import android.util.Log;

import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.Profile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * The class which manages the experiment the user wishes to publish and handles all logic associated with the task
 */
public class PublishFragmentManager {
    private int minNTrials;
    private GeoLocation geoLocation;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Profile profile;
    private Experiment experiment;

    /**
     *Constructor which receives the input from the UI and converts the minimum number of trials into int and lat/lon pair into double
     * Then accesses firebase and retrieves the name and cellphone associated with the UUID
     * @param userID
     * The UUID of the user publishing the experiment
     * @param desc
     * The Experiment Description
     * @param title
     * The experiment title
     * @param MinNTrialsString
     * The string containing an integer describing the minimum number of trials
     * @param lat
     * The latitude
     * @param lon
     * The longitude
     * @param radius
     * The radius of the region.
     * @param regionTitle
     * The title of the region
     * @param experimentType
     * The type of the experiment
     * @param reqLocation
     * Whether or not the user wants to enforce that trials must be added from within the region
     */
    public PublishFragmentManager(String userID,String desc, String title, String MinNTrialsString,
                                    double lat, double lon, double radius, String regionTitle, String experimentType, boolean reqLocation){
        try {
            minNTrials = Integer.parseInt(MinNTrialsString);
        }catch(NumberFormatException e){
            minNTrials = 0;
        }
        try{
            geoLocation= new GeoLocation(lat, lon, radius, regionTitle);
        }catch (NumberFormatException e){
            geoLocation = new GeoLocation();
        }
        firestore.collection("users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        String name= (String) document.get("name");
                        String cellphone = (String) document.get("cellphone");
                        String experimentID= UUID.randomUUID().toString();
                        profile = new Profile(userID,name,cellphone);
                        experiment= new Experiment(experimentID,experimentType,profile,title,desc,geoLocation,reqLocation,minNTrials);
                        Log.d("Data", document.getId() + " => " + document.getData());
                        FirebaseManager manager = new FirebaseManager();
                        manager.uploadExperiment(experiment);
                    }
                });
    }
}
