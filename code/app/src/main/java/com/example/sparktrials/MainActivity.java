package com.example.sparktrials;


import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sparktrials.main.publish.PublishFragment;
import com.example.sparktrials.models.Trial;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * The startup of the app. Sets up the tabview, topbar, and initializes the user
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request Location Permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.top_app_bar);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_app_bar_scan_qr_code:
                        Log.d("BUTTON", "scanClicked");
                        Intent intent = new Intent(MainActivity.this, QrScannerActivity.class);
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

        NavigationUI.setupWithNavController(navView, navController);
        IdManager idManager = new IdManager(this);
        idManager.login();
    }

}