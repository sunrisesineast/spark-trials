package com.example.sparktrials.main.search;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.sparktrials.ExperimentActivity;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class runs Intent Tests to test the functionality of SearchFragment.
 */

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {

    private Solo solo;

    private FirebaseFirestore db;
    String expTitle;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        db = FirebaseFirestore.getInstance();
        expTitle = "SearchTest";

        // Add experiment
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        solo.enterText((EditText)solo.getView(R.id.expTitle_editText), expTitle);
        solo.clickOnButton("POST");
        assertTrue(solo.waitForText(expTitle, 1, 2000));
    }


    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        solo.clickOnView(solo.getView(R.id.navigation_search));
    }

    /**
     * Checks whether experiments show when we navigate to the search page
     *
     * @throws Exception
     */
    @Test
    public void checkExperimentsShowing() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_search));

        assertTrue(solo.waitForText(expTitle, 1, 2000));
    }

    /**
     * Checks functionality of the search feature
     * @throws Exception
     */
    @Test
    public void checkSearchWorking() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_search));

        // At time of testing, there exists no experiments in Firestore that should match the
        // following word
        String wordDoesNotExist = "qwertyuiop";
        solo.enterText((EditText) solo.getView(R.id.search_bar), wordDoesNotExist);

        solo.clearEditText((EditText) solo.getView(R.id.search_bar));
        assertFalse(solo.waitForText(wordDoesNotExist, 1, 1000));

        // Changing to lower case as search should match fields even if they're in lower case
        solo.enterText((EditText) solo.getView(R.id.search_bar), expTitle.toLowerCase());
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.clearEditText((EditText) solo.getView(R.id.search_bar));
        assertTrue(solo.waitForText(expTitle, 1, 1000));
    }

    /**
     * Checks that an ExperimentActivity is launched when a search result is clicked on.
     * @throws Exception
     */
    @Test
    public void checkLaunchActivity() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_search));

        solo.enterText((EditText) solo.getView(R.id.search_bar), expTitle);
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.clearEditText((EditText) solo.getView(R.id.search_bar));
        assertTrue(solo.waitForText(expTitle, 1, 1000));
        solo.clickInList(0, 0); // Clicking the first item in the list after the search

        // Making sure ExperimentActivity is launched
        solo.assertCurrentActivity("Wrong activity", ExperimentActivity.class);

        // Making sure the ExperimentActivity has information of the corresponding Experiment
        // clicked on
        ExperimentActivity activity = (ExperimentActivity) solo.getCurrentActivity();
        final TextView experimentTitleTextView = activity.findViewById(R.id.text_title);
        solo.waitForText(expTitle, 1, 1000);
        String experimentActivityTitle = experimentTitleTextView.getText().toString();
        assertTrue(experimentActivityTitle, expTitle.equals(experimentActivityTitle));
    }

    /**
     * Close activity after each test.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
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
