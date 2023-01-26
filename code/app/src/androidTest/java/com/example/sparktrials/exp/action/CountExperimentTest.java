package com.example.sparktrials.exp.action;

import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CountExperimentTest {
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
        expTitle = "CountTrialTest";
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Get owner's username
        solo.clickOnView(solo.getView(R.id.navigation_me));
        expOwnerId =  ((TextView) solo.getView(R.id.user_id)).getText().toString();
        solo.clickOnView(solo.getView(R.id.navigation_home));

        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        Spinner spinner = (Spinner) solo.getView(R.id.experiment_type_spinner);
        spinner.setSelection(1,true);
        solo.enterText((EditText) solo.getView(R.id.expTitle_editText),expTitle);
        solo.clickOnButton("POST");
        solo.clickOnView(solo.getView(R.id.navigation_search));
        solo.enterText((EditText) solo.getView(R.id.search_bar),expTitle);
        solo.clickInList(0,0);
    }

    /**
     * Deletes Trials and make sure UI is updated when entered trials are deleted
     */
    @Test
    public void deleteTrials(){
        TextView trialsCount = (TextView) solo.getView(R.id.trials_count);
        String textBefore = trialsCount.toString();
        solo.clickOnButton("Add Count");
        solo.sleep(1000);
        solo.clickOnButton("Delete Trials");
        solo.sleep(1000);
        String textAfter = trialsCount.toString();
        assertTrue(textBefore.equals(textAfter));
    }

    /**
     * Tests the increment count button
     */
    @Test
    public void incrementCount(){
        TextView trialsCount = (TextView) solo.getView(R.id.trials_count);
        solo.sleep(1000);
        solo.clickOnButton("Add Count");
        solo.sleep(1000);;
    }

    /**
     * Tests the upload trials button
     */
    @Test
    public void uploadTrials(){
        TextView trialsCount = (TextView) solo.getView(R.id.trials_count);
        solo.sleep(1000);
        solo.clickOnButton("Add Count");
        solo.sleep(1000);;
        solo.clickOnButton("Upload Trials");
        solo.sleep(1000);;
    }

    /**
     * Deletes the test experiment
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
