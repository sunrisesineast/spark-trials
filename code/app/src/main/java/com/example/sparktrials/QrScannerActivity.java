package com.example.sparktrials;

/**
 * This class handles any time that the Qr/Bar code scanner is used in the app.
 * Either for scanning a previously generated QRCode or previously registered Barcode
 * Or for registering a barcode for use in adding trials.
 *
 * Each of the methods called during the running of this class are clled from within the asynchronous
 * get calls from the database to avoid any timing issues with data not being retrieved in time
 * e.g. createTrial is called first once the code is scanned and decoded. Then downloadExperiment
 * is not called until the end of the callback function inside createTrial() so that we ensure the
 * trial is created before we attempt to download the experiment.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.QrCode;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;
import com.example.sparktrials.models.TrialCount;
import com.example.sparktrials.models.TrialIntCount;
import com.example.sparktrials.models.TrialMeasurement;
import com.google.firebase.Timestamp;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrScannerActivity extends AppCompatActivity {

    FirebaseManager db = new FirebaseManager();
    private String userId;
    private MutableLiveData<Experiment> exp = new MutableLiveData<>();
    private MutableLiveData<Trial> trial = new MutableLiveData<>();
    private MutableLiveData<Profile> profile = new MutableLiveData<>();
    private MutableLiveData<GeoLocation> currentLocation = new MutableLiveData<>();

    LocationManager locationManager;
    // scanReg = 1 when the scanner is being used to register a new barcode to the system
    // scanReg = 0 when the scanner is being used to scan an already existing code to create a trial
    private int scanReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
        IdManager idManager = new IdManager(this);
        userId = idManager.getUserId();
        downloadProfile();

        scanReg = getIntent().getIntExtra("ScanReg", 0);

        IntentIntegrator qrIntegrator = new IntentIntegrator(this);
        if(scanReg == 0){
            qrIntegrator.setPrompt("Scan a QRCode or Registered Bar Code");
        } else {
            qrIntegrator.setPrompt("Scan a Barcode to register");
        }
        qrIntegrator.setOrientationLocked(true);
        qrIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String codeResult = intentResult.getContents();
        if(codeResult == null && resultCode != RESULT_OK){
            Log.d("Scanner", "User hit back button");
            finish();
            return;
        }
        if(scanReg == 0) {
            // here we check to see if the decoded value of the code is a UUID or not
            // If it is we treat it as a QRCode that decoded into it's Id in the database
            // If it isn't we use nameUUIDFromBytes to turn in into a UUID that would have been
            // used to reference a generic barcode in the database
            Pattern uuidPattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
            Matcher uuidMatcher = uuidPattern.matcher(codeResult);
            String codeId;
            if(!uuidMatcher.matches()){
                codeId = UUID.nameUUIDFromBytes(codeResult.getBytes()).toString();
            } else {
                codeId = codeResult;
            }

            createTrial(codeId);
        } else {
            // use nameUUIDFromBytes to have a consistent way of getting the same UUID next time
            // this barcode is scanned
            String codeId = UUID.nameUUIDFromBytes(codeResult.getBytes()).toString();
            String experimentId = getIntent().getStringExtra("ExpId");
            String trialType = getIntent().getStringExtra("TrialType");
            double value = getIntent().getDoubleExtra("Value", 0.0);
            QrCode newCode = new QrCode(codeId, experimentId, trialType, value);
            uploadCode(newCode);
            finish();
        }

    }

    /**
     * Uploads the Data of a qr code or barcode to the database
     * @param code
     *  The QrCode objet to be uploaded
     */
    public void uploadCode(QrCode code){
        Map<String, Object> map = new HashMap<>();
        map.put("Id", code.getQrId());
        map.put("ExperimentId", code.getExperimentId());
        map.put("TrialType", code.getTrialType());
        map.put("Value", code.getValue());
        db.set("qrCodeData", code.getQrId(), map);
    }

    /**
     * Download the user's profile from the databae, as it is needed to create a trial
     */
    public void downloadProfile() {
        db.get("users", userId, proData -> {
            Profile prof = new Profile(userId);

            prof.setUsername((String) proData.getData().get("name"));
            prof.setContact((String) proData.getData().get("contact"));
            prof.setSubscriptions((ArrayList<String>) proData.getData().get("subscriptions"));

            profile.setValue(prof);
        });
    }

    /**
     * Generate a new trial from a given Qr/Bar code Id
     * @param qrId
     *  The id of the Qr/Bar code
     */
    public void createTrial(String qrId) {
        db.get("qrCodeData", qrId, qrData -> {
            String trialType;
            try{
                trialType = (String) qrData.getData().get("TrialType");
            } catch(Exception e){
                finish();
                return;
            }
            Trial tri;
            Double value = (Double) qrData.getData().get("Value");
            if(trialType.equals("binomial trials")){
                tri = new TrialBinomial(value == 1.0 ? true : false);
            } else if(trialType.equals("counts")){
                tri = new TrialCount();
            } else if(trialType.equals("non-negative integer counts")) {
                tri = new TrialIntCount(value.intValue());
            } else {
                tri = new TrialMeasurement(value);
            }

            tri.setProfile(profile.getValue());
            tri.setId(UUID.randomUUID().toString());
            trial.setValue(tri);
            getExperiment((String) qrData.getData().get("ExperimentId"));
        });
    }

    /**
     * Retrieve the experiment the trial is for from the database
     * @param expId
     *  The id of the experiment
     */
    public void getExperiment(String expId){
        db.get("experiments", expId, expData -> {
            Experiment experiment = new Experiment(expId);

            experiment.setTitle((String) expData.getData().get("Title"));
            experiment.setDesc((String) expData.getData().get("Description"));
            GeoLocation region = new GeoLocation();
            region.setLat((Double) expData.getData().get("Latitude"));
            region.setLon((Double) expData.getData().get("Longitude"));
            region.setRadius((Double) expData.getData().get("Radius"));
            experiment.setRegion(region);
            experiment.setReqLocation((Boolean) expData.getData().get("ReqLocation"));
            experiment.setMinNTrials(((Long) expData.getData().get("MinNTrials")).intValue());
            Timestamp date = (Timestamp) expData.getData().get("Date");
            experiment.setDate(date.toDate());
            experiment.setOpen((Boolean) expData.getData().get("Open"));
            experiment.setType((String) expData.getData().get("Type"));
            String uId = (String) expData.getData().get("profileID");
            String username = (String) expData.getData().get("ownerName");
            Profile owner = new Profile(uId);
            owner.setUsername(username);
            experiment.setOwner(owner);
            ArrayList<HashMap> trialsHash = (ArrayList<HashMap>) expData.getData().get("Trials");
            ArrayList<Trial> trials = new ArrayList<>();
            for(HashMap<String, Object> map: trialsHash){
                Trial trial;
                String type = experiment.getType();
                if(type.equals("binomial trials")){
                    if((Double) map.get("value") == 0.0){
                        trial = new TrialBinomial(false);
                    } else {
                        trial = new TrialBinomial(true);
                    }

                } else if(type.equals("non-negative integer counts")) {
                    trial = new TrialIntCount(((Double) map.get("value")).intValue());
                } else if(type.equals("measurement trials")){
                    trial = new TrialMeasurement((Double) map.get("value"));
                } else {
                    trial = new TrialCount();
                }

                if (experiment.hasLocationSet()) {
                    HashMap<String, Object> trialCoords = (HashMap<String, Object>) map.get("location");
                    GeoLocation trialLocation = new GeoLocation();
                    trialLocation.setLat((double) trialCoords.get("lat"));
                    trialLocation.setLon((double) trialCoords.get("lon"));
                    trial.setLocation(trialLocation);
                }

                Profile experimenter = new Profile();
                HashMap<String, Object> profile = (HashMap<String, Object>) map.get("profile");
                experimenter.setContact((String) profile.get("contact"));
                experimenter.setUsername((String) profile.get("username"));
                experimenter.setId((String) profile.get("id"));
                experimenter.setSubscriptions((ArrayList<String>) profile.get("subscriptions"));
                trial.setProfile(experimenter);
                trial.setId((String) map.get("id"));
                Timestamp trialDate = (Timestamp) map.get("date");
                trial.setDate(trialDate.toDate());
                trials.add(trial);
            }
            experiment.setTrials(trials);
            experiment.setBlacklist((ArrayList<String>) expData.getData().get("Blacklist"));

            exp.setValue(experiment);
            if(experiment.getReqLocation()){
                getLocation();
            }
            uploadTrial();
        });

    }

    /**
     * Add the generated trial to the experiment
     * Trials are only added if they could be added by a user normally through the action tab
     * I.E: if the experiment is closed or unpublished, the trial is not added,
     * If a user is not subscribed to the experiment, they will be subscribed to it prior to adding
     * the trial
     * If the experiment requires location then the user's location is added to the trial
     * If the experiment has a region defined, then a user can only add a trial from within that region
     */
    public void uploadTrial(){
        Experiment experiment = exp.getValue();
        Trial tri = trial.getValue();
        Profile pro = profile.getValue();

        //Do not add trials to unpublished experiments
        if(!experiment.getPublished()){
            Toast.makeText(getApplicationContext(), "That experiment is unpublished", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //Do not add trials to closed experiments
        if(!experiment.getOpen()){
            Toast.makeText(getApplicationContext(), "That experiment is currently closed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // if the user is not subscribed, subscribe them
        if(!pro.getSubscriptions().contains(experiment.getId())){
            pro.addSubscription(experiment.getId());
            HashMap<String, Object> map = new HashMap<>();
            map.put("subscriptions", pro.getSubscriptions());
            db.update("users", pro.getId(), map);
        }

        // if the experiment has locations enabled, attach the current location to the trial
        if(experiment.hasLocationSet()){
            tri.setLocation(currentLocation.getValue());
            // if the experiment requires trials to be in a range, check whether the trial is
            // within that range, if it isn't. do not add the trial
            if(experiment.getReqLocation()){
                if(!isWithinRegion(tri, experiment)){
                    Toast.makeText(getApplicationContext(), "You are outside the range of this experiment", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

        }

        // If all conditions for adding a trial are met, add the trial
        experiment.addTrial(tri);
        db.uploadTrials(experiment);
        finish();
    }

    //Location listener for the QrCode Scanner
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            GeoLocation loc = new GeoLocation(location.getLatitude(), location.getLongitude());

            currentLocation.setValue(loc);
        }
    };

    // this is called whether the experiment needs location or not, however the location
    // is only used if the experiment has locations turned on.

    /**
     * Method to retrieve the location of the user.
     * This is called whether the experiment needs location or not, however the location
     * is only used if the experiment has locations turned on.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * function to calculate the distance between a trial location and the center of an
     * experiment region
     * @param trial
     *  The trial to be checked
     * @param experiment
     *  The experiment with the range
     * @return
     *  The distance from trial location to center of experiment range
     */
    public double calculateDistance(Trial trial, Experiment experiment){
        // Haversine formula is used here
        // All distances in meters, angles in radians

        double eLat = experiment.getRegion().getLat();
        double eLon = experiment.getRegion().getLon();
        double tLat = trial.getLocation().getLat();
        double tLon = trial.getLocation().getLon();

        final double R = 6371000; //mean earth radius

        // Convert angles to radians
        final double phi1 = eLat * Math.PI/180;
        final double phi2 = tLat * Math.PI/180;
        final double deltaPhi = (tLat - eLat) * Math.PI/180;
        final double deltaLambda = (tLon - eLon) * Math.PI/180;

        // Angular distance in radians between two points
        final double a = Math.pow(Math.sin(deltaPhi/2), 2) + (Math.cos(phi1) * Math.cos(phi2)
                * Math.pow(Math.sin(deltaLambda/2), 2));

        // Square of half the chord length between the two points
        final double c = Math.atan2(Math.sqrt(a), Math.sqrt(1-a)) * 2;

        final double distance = R*c;
        return distance;
    }

    /**
     * return whether or not a trial is within range of an experiment
     * @param trial
     *  The trail in question
     * @param experiment
     *  The experiment in question
     * @return
     *  True if the trial is within experiment region
     *  False if not
     */
    public boolean isWithinRegion(Trial trial, Experiment experiment){
        return experiment.getRegion().getRadius() >= calculateDistance(trial, experiment);
    }

}
