package com.example.sparktrials.main.publish;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.sparktrials.MainActivity;
import com.example.sparktrials.R;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests aspects of the publishing new experiment UI
 */
@RunWith(AndroidJUnit4.class)
public class PublishFragmentTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Checks that the dialog shows up when publish experiment is pressed
     */
    @Test
    public void checkDialogExistence(){
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        assertTrue("Dialog is showing",solo.searchText("Publish Experiment"));
    }

    /**
     * Checks that the cancel button removes the dialog
     */
    @Test
    public void testCancelButton(){
        solo.clickOnView(solo.getView(R.id.top_app_bar_publish_experiment));
        solo.clickOnButton("X");
        solo.sleep(1000);
        assertFalse("Dialog isnt showing",solo.searchText("Add Experiment"));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

}
