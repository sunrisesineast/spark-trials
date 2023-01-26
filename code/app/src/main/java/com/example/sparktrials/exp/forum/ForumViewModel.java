package com.example.sparktrials.exp.forum;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sparktrials.Callback;
import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.models.Answer;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A class to manage the data for ForumFragment
 */
public class ForumViewModel extends ViewModel {
    private String experimentId;
    private MutableLiveData<ArrayList<Question>> questions;
    private FirebaseFirestore db;
    private final String LOG_TAG = "ForumViewModel";

    /**
     * Constructor for ForumViewModel
     */
    public ForumViewModel(String experimentId) {
        this.experimentId = experimentId;
        questions = new MutableLiveData<>();
        questions.setValue(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        getForumQuestions();
    }

    /**
     * Get Forum Questions from Firestore.
     */
    private void getForumQuestions() {
        CollectionReference ref = db.collection("experiments").document(experimentId).collection("posts");
        FirebaseManager firebaseManager = new FirebaseManager();
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_TAG, "Questions Listen failed.", error);
                    return;
                }
                ArrayList<Question> newQuestions = new ArrayList<>();
                for (QueryDocumentSnapshot document: value) {
                    String id = document.getId();
                    String title = (String) document.get("title");
                    String body = (String) document.get("body");
                    String author = (String) document.get("author");
                    Timestamp timestamp = (Timestamp) document.get("date");

                    firebaseManager.get("users", author, new Callback() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onCallback(DocumentSnapshot document) {
                            String name = (String) document.get("name");
                            String contact = (String) document.get("contact");
                            Profile profile = new Profile(document.getId(), name, contact);
                            Question question = new Question(id, title, body, experimentId, profile, timestamp.toDate());

                            newQuestions.add(question);
                            questions.setValue(newQuestions);
                            getPostAnswers(question);
                        }
                    });
                }

            }
        });
    }


    /**
     * Get Answers from a given question on Firestore.
     * @param question
     */
    public void getPostAnswers(Question question) {
        CollectionReference ref = db.collection("experiments").document(experimentId)
                .collection("posts").document(question.getId()).collection("comments");
        FirebaseManager firebaseManager = new FirebaseManager();

        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_TAG, "Answers Listen failed.", error);
                    return;
                }

                ArrayList<Answer> newAnswers = new ArrayList<>();
                for (QueryDocumentSnapshot document: value) {
                    String id = document.getId();
                    String body = (String) document.get("body");
                    String author = (String) document.get("author");
                    Timestamp timestamp = (Timestamp) document.get("date");

                    firebaseManager.get("users", author, new Callback() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onCallback(DocumentSnapshot document) {
                            String name = (String) document.get("name");
                            String contact = (String) document.get("contact");
                            Profile profile = new Profile(document.getId(), name, contact);

                            Answer answer = new Answer(id, body, experimentId, profile, question, timestamp.toDate());
                            ArrayList<Question> existingQuestions = questions.getValue();
                            newAnswers.add(answer);

                            existingQuestions.get(existingQuestions.indexOf(question)).setAnswers(newAnswers);
                            // Sort answers by earliest first
                            existingQuestions.get(existingQuestions.indexOf(question)).sortAnswersLatestFirst(false);
                            // Sort questions by latest first
                            existingQuestions.sort((d1,d2) -> d1.compareTo(d2));
                            Collections.sort(existingQuestions, Collections.reverseOrder());
                            questions.setValue(existingQuestions);
                        }
                    });

                }

            }
        });
    }

    /**
     * Get Questions as a MutableLiveData of Arraylist.
     * @return MutableLiveData object of an Arraylist of QUestions.
     */
    public MutableLiveData<ArrayList<Question>> getQuestions() {
        return questions;
    }
    
}
