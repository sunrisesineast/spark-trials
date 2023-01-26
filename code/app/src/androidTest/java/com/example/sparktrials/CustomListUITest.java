package com.example.sparktrials;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the custom list class
 */
@RunWith(AndroidJUnit4.class)
public class CustomListUITest {
    private Solo solo;

    private FirebaseFirestore db;
    String expTitle, expDescription, expOwnerId;

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
        expTitle = "CustomListTest";
        expDescription = "CustomListDescTest";

        // Get owner's username
        solo.clickOnView(solo.getView(R.id.navigation_me));
        expOwnerId =  ((TextView) solo.getView(R.id.user_id)).getText().toString();
        solo.clickOnView(solo.getView(R.id.navigation_home));

        // Add experiment
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        solo.enterText((EditText)solo.getView(R.id.expTitle_editText), expTitle);
        solo.enterText((EditText)solo.getView(R.id.expDesc_editText), expDescription);
        solo.clickOnButton("POST");
        assertTrue(solo.waitForText(expTitle, 1, 2000));
        assertTrue(solo.waitForText(expDescription, 1, 2000));
    }

    /**
     * Checks that a ProfileActivity is launched when a username is clicked on.
     * @throws Exception
     */
    @Test
    public void checkLaunchActivity() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        TextView usernameListTextView = ((ListView) solo.getView(R.id.myExperiment_list))
                                        .getChildAt(0).findViewById(R.id.list_experiment_owner);
        solo.clickOnView(usernameListTextView);

        // Making sure ExperimentActivity is launched
        solo.assertCurrentActivity("Wrong activity", ProfileActivity.class);

        // Making sure the ExperimentActivity has information of the corresponding Experiment
        // clicked on
        ProfileActivity activity = (ProfileActivity) solo.getCurrentActivity();
        final TextView userIDTextView = activity.findViewById(R.id.experimenter_id);
        solo.waitForText(expOwnerId, 1, 1000);
        String userID = userIDTextView.getText().toString();
        assertTrue("User ID matches", expOwnerId.equals(userID));
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
