package com.example.sparktrials.main.search;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This class acts as a manager for SearchFragment.
 */
public class SearchViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Experiment>> experiments;

    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference experimentsCollection = db.collection("experiments");
    private CollectionReference usersCollection = db.collection("users");

    final String TAG = "Fetching documents...";

    /**
     * Constructor for SearchViewModel
     */
    public SearchViewModel() {
        experiments = new MutableLiveData<>();
        getExperimentsFromDB();
    }


    /**
     * Gets the list of experiments in the database
     * @return
     *      Returns the list of experiments in the database
     */
    public MutableLiveData<ArrayList<Experiment>> getExperiments() {
        return experiments;
    }

    /**
     * Gets an updated list of experiments from the database.
     */
    private void getExperimentsFromDB() {

        experimentsCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            experiments.setValue(new ArrayList<>());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Boolean published = (Boolean) document.get("Published");
                                if (published) {
                                    // Get all the published experiments and set their respective attributes
                                    String id = document.getId();
                                    String title = (String) document.get("Title");
                                    String desc = (String) document.get("Description");
                                    Boolean open = (Boolean) document.get("Open");
                                    Date date = document.getTimestamp("Date").toDate();

                                    Experiment experiment = new Experiment(id);
                                    experiment.setTitle(title);
                                    experiment.setDesc(desc);
                                    experiment.setOpen(open);
                                    experiment.setPublished(true); // Always true because of the if-statement
                                    experiment.setDate(date);

                                    String ownerId = (String) document.get("profileID");

                                    // Get details of the owner of the experiment and set their attributes
                                    usersCollection.document(ownerId).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        String username = (String) document.get("name");
                                                        String phoneNum = (String) document.get("contact");

                                                        Profile owner = new Profile(ownerId);
                                                        owner.setUsername(username);
                                                        owner.setContact(phoneNum);

                                                        experiment.setOwner(owner);

                                                        // Add the experiment (with all it's info and
                                                        // owner info) to the list of experiments
                                                        ArrayList<Experiment> x = experiments.getValue();
                                                        x.add(experiment);

                                                        // Updates the value of experiments so that
                                                        // the observer in SearchFragment updates the UI
                                                        experiments.setValue(x);
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Searches through the list of experiments (from the database) and finds any experiments that
     * match at least one of the keywords.
     * @param keywords
     *      This is the array of keywords that we will check the experiments against
     * @param filters
     *      The filters applied to the search. It will be of size 2. The first integer will
     *      filter based on Title, Description, or Username: 0 for no filter, 1 for title,
     *      2 for description, and 3 for username. The second will filter based on
     *      status, 0 for no filter, 1 for active status, 2 for inactive status.
     * @return
     *      Returns the list of experiments that match at least one of the keywords, after applying
     *      the filters.
     */
    public ArrayList<Experiment> search(String[] keywords, int[] filters) {

        ArrayList<Experiment> experimentsInDB = experiments.getValue();

        HashSet<Experiment> resultSet = new HashSet<>();  // To avoid duplicates

        if (keywords.length == 0) {
            if (filters[1] == 0) {
                return experimentsInDB;
            } else {
                // Get only experiments with the status that the user chose
                for (int experimentIndex = 0; experimentIndex < experimentsInDB.size(); experimentIndex++) {
                    Experiment experimentToBeSearched = experimentsInDB.get(experimentIndex);
                    if (experimentMatches("", experimentToBeSearched, filters)) {
                        // If an experiment matches a keyword
                        resultSet.add(experimentToBeSearched);
                    }
                }
            }
        } else {
            for (int experimentIndex = 0; experimentIndex < experimentsInDB.size(); experimentIndex++) {
                for (int keywordIndex = 0; keywordIndex < keywords.length; keywordIndex++) {
                    String currentKeyword = keywords[keywordIndex];
                    Experiment experimentToBeSearched = experimentsInDB.get(experimentIndex);
                    if (experimentMatches(currentKeyword, experimentToBeSearched, filters)) {
                        // If an experiment matches a keyword
                        resultSet.add(experimentToBeSearched);
                    }
                }
            }
        }

        ArrayList<Experiment> results = new ArrayList<>();
        results.addAll(resultSet);

        return results;
    }

    /**
     * Checks whether the experiment has fields that match a keyword.
     * @param keyword
     *      The keyword we want to check the experiment fields against.
     * @param experiment
     *      The experiment whose fields we want to check.
     * @param filters
     *      The filters applied to the search. It will be of size 2.
     *      The first integer will filter based on Title, Description, or Username: 0 for no filter,
     *      1 for title, 2 for description, and 3 for username.
     *      The second integer will filter based on
     *      status, 0 for no filter, 1 for active status, 2 for inactive status.
     * @return
     *      true if the experiment has matching fields, false otherwise.
     */
    private boolean experimentMatches(String keyword,
                                      Experiment experiment, int[] filters) {
        String experimentTitle = experiment.getTitle().toLowerCase();
        String experimentDescription = experiment.getDesc().toLowerCase();
        String experimentOwnerUsername = experiment.getOwner().getUsername().toLowerCase();
        boolean experimentStatus = experiment.getOpen();

        boolean matches = false;

        if (filters[0] == 0) { // no field filter
            if (experimentTitle.contains(keyword) || experimentDescription.contains(keyword) || experimentOwnerUsername.contains(keyword)) {
                switch (filters[1]) {
                    case 0: matches = true; break;
                    case 1: matches = experimentStatus; break;
                    case 2: matches = !experimentStatus; break;
                }
            }
        } else if (filters[0] == 1) { // filter based on title
            if (experimentTitle.contains(keyword)) {
                switch (filters[1]) {
                    case 0: matches = true; break;
                    case 1: matches = experimentStatus; break;
                    case 2: matches = !experimentStatus; break;
                }
            }
        } else if (filters[0] == 2) { // filter based on description
            if (experimentDescription.contains(keyword)) {
                switch (filters[1]) {
                    case 0: matches = true; break;
                    case 1: matches = experimentStatus; break;
                    case 2: matches = !experimentStatus; break;
                }
            }
        } else { // filters[0] == 3, filter based on username
            if (experimentOwnerUsername.contains(keyword)) {
                switch (filters[1]) {
                    case 0: matches = true; break;
                    case 1: matches = experimentStatus; break;
                    case 2: matches = !experimentStatus; break;
                }
            }
        }

        return matches;
    }

}

