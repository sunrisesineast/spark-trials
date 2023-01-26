package com.example.sparktrials;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.sparktrials.exp.ViewPagerAdapter;
import com.example.sparktrials.exp.action.ActionFragment;
import com.example.sparktrials.exp.admin.AdminFragment;
import com.example.sparktrials.exp.forum.ForumFragment;
import com.example.sparktrials.exp.location.LocationFragment;
import com.example.sparktrials.exp.stats.StatsFragment;
import com.example.sparktrials.main.publish.PublishFragment;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.google.android.material.tabs.TabLayout;

/**
 * A class with tabs for each ability of an experiment
 * Displays the title, description, owner, and status of the experiment,
 * also the subscribe button
 */
public class ExperimentActivity extends AppCompatActivity {

    private ExperimentViewModel expManager;
    private String userId;
    private String experimentId;

    private TabLayout tablayout;
    private ViewPager viewPager;

    private Button subscribe;
    private ImageButton backToMain;
    private TextView titleText;
    private TextView activeText;
    private TextView ownerNameText;
    private ImageView ownerIcon;
    private TextView descText;

    private String textSubscribe = "Subscribe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        //sets up the topbar
        Toolbar myToolbar = findViewById(R.id.top_app_bar);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_app_bar_scan_qr_code:
                        Log.d("BUTTON", "scanClicked");
                        Intent intent = new Intent(ExperimentActivity.this, QrScannerActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.top_app_bar_publish_experiment:
                        Log.d("BUTTON", "publishClicked");
                        new PublishFragment().show(getSupportFragmentManager(), "Add Experiment");
                        break;
                    default:
                        Log.d("BUTTON", "something wrong");
                }
                return true;
            }
        });

        //unpacks info from mainactivity and sets up UI
        experimentId = getIntent().getStringExtra("EXPERIMENT_ID");
        IdManager idManager = new IdManager(this);
        userId = idManager.getUserId();
        expManager = new ViewModelProvider(this).get(ExperimentViewModel.class);
        expManager.init(experimentId, userId);

        subscribe = findViewById(R.id.button_subscribe);
        backToMain = findViewById(R.id.back_button);
        titleText = findViewById(R.id.text_title);
        descText = findViewById(R.id.text_desc);
        descText.setMovementMethod(new ScrollingMovementMethod());
        activeText = findViewById(R.id.text_active);
        ownerNameText = findViewById(R.id.text_owner_name);
        ownerIcon = findViewById(R.id.owner_icon);

        tablayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // This will allow the subscription button to dynamically update
        final Observer<Profile> name1Observer = new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile.getSubscriptions().contains(experimentId)){
                    subscribe.setText("Unsubscribe");
                } else {
                    subscribe.setText("Subscribe");
                }
            }
        };
        expManager.getProfile().observe(this, name1Observer);
        expManager.subscribe().observe(this, name1Observer);

        // This will allow the experiment to be displayed when the activity is launched and firebase has retrieved the info
        final Observer<Experiment> name2Observer = new Observer<Experiment>() {

            @Override
            public void onChanged(Experiment experiment) {
                if (!expManager.getProfile().getValue().getSubscriptions().contains(experiment.getId())
                        && experiment.hasLocationSet()
                        && !experiment.getOwner().getId().equals(expManager.getProfile().getValue().getId())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExperimentActivity.this);
                    builder.setTitle("This experiment requires your location to be recorded");
                    builder.setNeutralButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.neutral));
                }

                titleText.setText(experiment.getTitle());
                descText.setText(experiment.getDesc());
                if (experiment.getOpen()) {
                    activeText.setText("Active");
                    activeText.setTextColor(getResources().getColor(R.color.positive));
                } else {
                    activeText.setText("Inactive");
                    activeText.setTextColor(getResources().getColor(R.color.neutral));
                }
                ownerNameText.setText(experiment.getOwner().getUsername());

                adapter.addFragment(new ActionFragment(experiment), "Action");
                adapter.addFragment(new StatsFragment(experiment), "Stats");
                adapter.addFragment(new ForumFragment(experiment), "Forum");
                if (experiment.getRegion().getRadius() > 0 && hasInternetConnectivity()) {
                    adapter.addFragment(new LocationFragment(experiment), "Map");
                }
                if (experiment.getOwner().getId().equals(userId)) {
                    AdminFragment a_frag = new AdminFragment(experiment);
                    a_frag.setActiveText(activeText);
                    adapter.addFragment(a_frag, "Admin");
                }
                viewPager.setAdapter(adapter);
                tablayout.setupWithViewPager(viewPager);
            }
        };
        expManager.getExperiment().observe(this, name2Observer);

        subscribe.setOnClickListener((v) -> {
            this.subscribe();
        });

        // Launches a ProfileActivity when the username of an Experiment's Owner is clicked on
        ownerNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(expManager.getExperiment().getValue().getOwner().getId());
            }
        });

        // Launches a ProfileActivity when the username of an Experiment's Owner is clicked on
        ownerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(expManager.getExperiment().getValue().getOwner().getId());
            }
        });

        //If back button is pressed, then go back to main activity
        backToMain.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        });

    }

    /**
     * Subscribes or unsubscribes the user to the experiment
     * If the experiment requires locations, sends a warning message
     */
    public void subscribe() {
        if (expManager.getExperiment().getValue().hasLocationSet() &&
                !expManager.getProfile().getValue().getSubscriptions().contains(experimentId)) {
            //Experiment requires locations and user is not currently subscribed
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This experiment requires your location to be recorded");
            builder.setMessage("Are you sure you want to subscribe?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //subscribe
                    expManager.subscribe();
                    expManager.updateSubscribe();
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.spark_text));
            alert.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.neutral));
        } else {
            //Experiment doesn't require locations or user is already subscribed
            //unsubscribe
            expManager.subscribe();
            expManager.updateSubscribe();
        }
    }

    /**
     * This method starts a ProfileActivity, which displays the information of the owner of an
     * experiment.
     * @param ownerId
     *      The user whose profile we want to display.
     */
    private void startProfileActivity(String ownerId) {
        Intent intent = new Intent(this.getBaseContext(), ProfileActivity.class);
        intent.putExtra("USER_ID", ownerId);
        startActivityForResult(intent, 0); // Throwaway requestCode
    }

    /**
     * This method checks if the user has an internet connection
     * @return
     *    A boolean value; true if internet connection, false if no internet connection
     */
    public boolean hasInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }
}
