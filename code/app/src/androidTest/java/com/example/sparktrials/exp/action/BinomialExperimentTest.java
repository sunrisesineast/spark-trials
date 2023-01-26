package com.example.sparktrials.exp.action;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.sparktrials.MainActivity;
import com.example.sparktrials.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BinomialExperimentTest {
    private Solo solo;
    private FirebaseFirestore db;
    String expTitle, expOwnerId;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Gets the user ID from the profile tab, then publishes an experiment sued for testing. then searches
     * for the experiment in the search tab and clicks on it
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        db = FirebaseFirestore.getInstance();
        expTitle = "BinomialExperimentTest";
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Get owner's username
        solo.clickOnView(solo.getView(R.id.navigation_me));
        expOwnerId =  ((TextView) solo.getView(R.id.user_id)).getText().toString();
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        Spinner spinner = (Spinner) solo.getView(R.id.experiment_type_spinner);
        spinner.setSelection(0,true);
        solo.enterText((EditText) solo.getView(R.id.expTitle_editText),expTitle);
        solo.clickOnButton("POST");
        solo.clickOnView(solo.getView(R.id.navigation_search));
        solo.enterText((EditText) solo.getView(R.id.search_bar),expTitle);
        solo.clickInList(0,0);
    }

    /**
     * Tests the add trial button
     */
    @Test
    public void addTrial(){
        for (int i=0 ; i<5; i++)
            solo.clickOnButton("Record Pass");
        solo.sleep(10000);
    }

    /**
     * Deletes the experiments used for testing
     * @throws InterruptedException
     */
    @After
    public void deleteExperiment() throws InterruptedException {
        CollectionReference expCollection = db.collection("experiments");


        expCollection.whereEqualTo("Title", expTitle).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc: task.getResult()) {
                            expCollection.document(doc.getId()).delete();
                        }
                    }
                });

        synchronized (solo) {
            solo.wait(2000);
        }
    }

}
