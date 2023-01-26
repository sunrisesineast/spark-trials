package com.example.sparktrials;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparktrials.main.publish.PublishFragment;
import com.example.sparktrials.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class represents an activity that displays a user's profile.
 */

public class ProfileActivity extends AppCompatActivity {

    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private CollectionReference usersCollection = db.collection("users");

    private ImageButton backToMain;

    private String userId;
    private MutableLiveData<Profile> userProfile;

    private TextView userNameTextView;
    private TextView userContactInfoTextView;
    private TextView userIdTextView;

    private String TAG = "Fetching Profile...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);Toolbar myToolbar = (Toolbar) findViewById(R.id.top_app_bar);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_app_bar_scan_qr_code:
                        Log.d("BUTTON", "scanClicked");
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

        backToMain = findViewById(R.id.back_button);

        userId = (String) getIntent().getExtras().get("USER_ID");

        userProfile = new MutableLiveData<>();

        userNameTextView = findViewById(R.id.experimenter_name);
        userContactInfoTextView = findViewById(R.id.experimenter_contact);
        userIdTextView = findViewById(R.id.experimenter_id);

        // Gets the user's profile information from the database
        usersCollection.document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Log.d(TAG, document.getId() + " => " + document.getData());
                            String userName = (String) document.get("name");
                            String userContactInfo = (String) document.get("contact");

                            Profile profile = new Profile(userId, userName, userContactInfo);

                            userProfile.setValue(profile);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });


        // This will allow the list of experiments to be displayed when the search fragment
        // is launched
        final Observer<Profile> nameObserver = new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable final Profile profile) {

                String userName = userProfile.getValue().getUsername();
                String userContactInfo = userProfile.getValue().getContact();
                String userId = userProfile.getValue().getId();

                userNameTextView.setText(userName);
                userContactInfoTextView.setText(userContactInfo);
                userIdTextView.setText(userId);
            }
        };
        userProfile.observe(this, nameObserver);

        backToMain.setOnClickListener((v) -> {
            finish();
        });

    }

}