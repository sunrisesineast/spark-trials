package com.example.sparktrials.main.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "Fetching documents...";
    private MutableLiveData<ArrayList<Experiment>> myExperiments;
    private MutableLiveData<ArrayList<Experiment>> subscribedExperiments;
    private ArrayList<String> eidList;
    private String profileID;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference experimentsCollection = db.collection("experiments");
    private final CollectionReference usersCollection = db.collection("users");

    public HomeViewModel(String pid) {
        profileID = pid;
        myExperiments = new MutableLiveData<>();
        subscribedExperiments = new MutableLiveData<>();
        getMyExperiments();
        getSubscribedExperiments();
    }
//    private void getExperimentsFromDB() {
//
//        experimentsCollection.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            experiments.setValue(new ArrayList<>());
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d(TAG, document.getId() + " => " + document.getData());
//                                String id = document.getId();
//                                String title = (String) document.get("Title");
//                                String desc = (String) document.get("Description");
//
//                                Experiment experiment = new Experiment(id);
//                                experiment.setTitle(title);
//                                experiment.setDesc(desc);
//
//                                String ownerId = (String) document.get("profileID");
//
//                                usersCollection.document(ownerId).get()
//                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    DocumentSnapshot document = task.getResult();
//                                                    String username = (String) document.get("name");
//                                                    String phoneNum = (String) document.get("contact");
//
//                                                    Profile owner = new Profile(ownerId);
//                                                    owner.setUsername(username);
//                                                    owner.setContact(phoneNum);
//
//                                                    experiment.setOwner(owner);
//
//                                                    ArrayList<Experiment> x = experiments.getValue();
//                                                    x.add(experiment);
//
//                                                    experiments.setValue(x);
//                                                }
//                                            }
//                                        });
//                            }
//                        } else {
//                            //Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }
        private void getMyExperiments() {
            experimentsCollection.whereEqualTo("profileID",profileID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                     @Override
                     public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                         if (error != null) {
                             Log.w(TAG, "Listen failed.",error);
                             return;
                         }
                         myExperiments.setValue(new ArrayList<>());
                         for(QueryDocumentSnapshot document: value) {
                             String id = document.getId();
                             String title = (String) document.get("Title");
                             String desc = (String) document.get("Description");
                             Boolean open = (Boolean) document.get("Open");
                             Date date = document.getTimestamp("Date").toDate();

                             Experiment exp = new Experiment(id);
                             exp.setTitle(title);
                             exp.setDesc(desc);
                             exp.setOpen(open);
                             exp.setDate(date);

                             usersCollection.document(profileID).get()
                                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                         if (task.isSuccessful()) {
                                             DocumentSnapshot document = task.getResult();
                                             String username = (String) document.get("name");
                                             String phoneNum = (String) document.get("contact");

                                             Profile owner = new Profile(profileID);
                                             owner.setUsername(username);
                                             owner.setContact(phoneNum);

                                             exp.setOwner(owner);

                                             ArrayList<Experiment> x = myExperiments.getValue();
                                             x.add(exp);

                                             myExperiments.setValue(x);
                                         }
                                     }
                                 });

                         }
                     }
                 });
        }
        private void getSubscribedExperiments() {
            usersCollection.document(profileID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot profile, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    subscribedExperiments.setValue(new ArrayList<>());
                    eidList = (ArrayList<String>) profile.get("subscriptions");
                    if (eidList != null) {
                        for (String eid : eidList) {
                            Experiment experiment = new Experiment();
                            DocumentReference dref = experimentsCollection.document(eid);
                            Log.w(TAG, eid);
                            dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot experimentDocument = task.getResult();
                                        Boolean published = (Boolean) experimentDocument.get("Published");
                                        if (published != null && published) {
                                            String id = experimentDocument.getId();
                                            String title = (String) experimentDocument.get("Title");

                                            String desc = (String) experimentDocument.get("Description");
                                            Date date = (Date) experimentDocument.getDate("Date", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                                            if (experimentDocument.getDate("Date", DocumentSnapshot.ServerTimestampBehavior.PREVIOUS) == null) {
                                                Log.w(TAG, "Could not return date");
                                            }
                                            Boolean open = (Boolean) experimentDocument.get("Open");

                                            experiment.setId(id);
                                            experiment.setTitle(title);
                                            experiment.setDesc(desc);
                                            experiment.setDate(date);
                                            experiment.setOpen(open);

                                            usersCollection.document((String) experimentDocument.get("profileID")).get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot profileDocument = task.getResult();

                                                            Profile owner = new Profile();
                                                            owner.setId(profileDocument.getId());
                                                            owner.setUsername((String) profileDocument.get("name"));
                                                            owner.setContact((String) profileDocument.get("contact"));
                                                            experiment.setOwner(owner);
                                                            ArrayList<Experiment> x = subscribedExperiments.getValue();
                                                            x.add(experiment);

                                                            subscribedExperiments.setValue(x);
                                                        }
                                                    });
                                        }
                                    }
                                }


                            });
                        }
                    }
                }

            });
        }

        public MutableLiveData<ArrayList<Experiment>> getMyExpList() {return myExperiments;}
        public MutableLiveData<ArrayList<Experiment>> getSubExpList() {return subscribedExperiments;}

}
