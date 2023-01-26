package com.example.sparktrials.main.me;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.sparktrials.MainActivity;
import com.example.sparktrials.R;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class runs Intent Tests to test the functionality of MeFragment.
 */

@RunWith(AndroidJUnit4.class)
public class MeFragmentTest {

    private Solo solo;
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
    }


    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        solo.clickOnView(solo.getView(R.id.navigation_me));
    }

    @Test
    public void checkEditInfo() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_me));

        String currentUserName =  ((TextView) solo.getView(R.id.tvName)).getText().toString();
        String currentContactInfo = ((TextView) solo.getView(R.id.experimenter_contact)).getText().toString();

        solo.clickOnView(solo.getView(R.id.btn_ep)); // Clicks on button to edit profile

        solo.clearEditText((EditText) solo.getView(R.id.et_name));
        solo.clearEditText((EditText) solo.getView(R.id.etContact));

        solo.enterText((EditText) solo.getView(R.id.et_name), "Some Username");
        solo.enterText((EditText) solo.getView(R.id.etContact), "Some Contact Info");
        solo.clickOnButton("SAVE");

        solo.waitForText("Some Username");
        solo.waitForText("Some Contact Info");

        solo.clickOnView(solo.getView(R.id.btn_ep)); // Clicks on button to edit profile

        solo.clearEditText((EditText) solo.getView(R.id.et_name));
        solo.clearEditText((EditText) solo.getView(R.id.etContact));

        solo.enterText((EditText) solo.getView(R.id.et_name), currentUserName);
        solo.enterText((EditText) solo.getView(R.id.etContact), currentContactInfo);
        solo.clickOnButton("SAVE");

        solo.waitForText(currentUserName);
        solo.waitForText(currentContactInfo);
    }

    /**
     * Close activity after each test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
