package com.example.sparktrials.main.home;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.sparktrials.ExperimentActivity;
import com.example.sparktrials.MainActivity;
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
import com.example.sparktrials.R;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the UI of the owned experiments and subscribed experiments tab of the homeFragment
 */
public class HomeFragmentTest {

    private Solo solo;

    private FirebaseFirestore db;
    private String expTitle;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        db  = FirebaseFirestore.getInstance();
        expTitle = "Home Fragment Test";

        // Add experiment
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        solo.enterText((EditText)solo.getView(R.id.expTitle_editText), expTitle);
        solo.clickOnButton("POST");
        assertTrue(solo.waitForText(expTitle, 1, 2000));
    }

    @Test
    public void testMyExperimentList(){
        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        assertTrue(solo.waitForText(expTitle, 1, 2000));
    }

    @Test
    public void testSubscribe(){
        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.clickOnButton("Subscribe");
        solo.goBack();
        solo.assertCurrentActivity("Wrong Actiity",MainActivity.class);
        solo.clickOnText("Subscribed Experiments");
        assertTrue(solo.waitForText(expTitle, 1, 2000));
    }

    /**
     * Deletes experiments that were created for testing purposes.
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
