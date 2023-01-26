package com.example.sparktrials;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.UUID;

/**
 * A singleton to keep track of a user's id. Accessible from anywhere
 */
public class IdManager {
    private Context context;
    private static boolean logged_in = false;

    /**
     * Constructor for IdManager
     * @param context the current context.
     */
    public IdManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Return the unique user id. Generate a random UUID if no id is found in the preference file.
     * Note: This user id is lost when the app is uninstalled.
     * @return
     * Return the user id as a string.
     */

    public String getUserId() {
        SharedPreferences sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "-1");

        if (userId.equals("-1")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userId", generateRandomId());
            editor.apply();
            userId = sharedPref.getString("userId", "-1");
        }
        return userId;
    }

    /**
     * Generate a random UUID
     * @return a String of a random UUID.
     */
    public String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Handles auto login when the app launches.
     */
    public void login() {
        if (logged_in)
            return;

        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.get("users", getUserId(), new Callback() {
            @Override
            public void onCallback(DocumentSnapshot document) {
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                if (document.exists()) {
                    toast.setText("Welcome back, " + document.getData().get("name"));
                    toast.show();
                    Log.d("USER INFO", "DocumentSnapshot data: " + document.getData());
                } else {
                    firebaseManager.createUserProfile(getUserId());
                    toast.setText("New user profile created");
                    toast.show();
                }
                logged_in = true;
            }
        });
    }

}
