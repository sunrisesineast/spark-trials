package com.example.sparktrials.exp.action;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.exp.DraftManager;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.QrCode;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;
import com.example.sparktrials.models.TrialCount;
import com.example.sparktrials.models.TrialIntCount;
import com.example.sparktrials.models.TrialMeasurement;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * When a user decides to upload a trial or generate a qr code or delete a trial action
 * fragment manager deals with that
 */
public class ActionFragmentManager {

    private Experiment experiment;
    private FirebaseManager firebaseManager = new FirebaseManager();
    private int originalNTrials;
    private String id;
    private Profile profile;
    private DraftManager draftManager;
    private Boolean draftsLoaded;

    /**
     * Sets a draftManager that stores trials
     * @param draftManager
     */
    public void setDraftManager(DraftManager draftManager) {
        draftManager.setExperiment(experiment);
        this.draftManager = draftManager;
        //checks if trials have already been loaded from file
        //if not already loaded it loads the trials into the experiment
        if (draftsLoaded == Boolean.FALSE) {
            addDraftsToExperiment();
            this.draftsLoaded = Boolean.TRUE;
        }

    }

    /**
     * Adds trials saved in file to the experiment
     */
    public void addDraftsToExperiment(){
        experiment.addTrials(draftManager.getDraft_trials());

    }

    public ActionFragmentManager(Experiment experiment) {
        this.id=id;
        this.experiment=experiment;
        this.originalNTrials=Integer.parseInt(experiment.getNumTrials());
        this.draftsLoaded = Boolean.FALSE;
    }

    /**
     * Sets the profile of the user
     * @param id
     */
    public void setProfile(String id){
        profile=firebaseManager.downloadProfile(id);
    }

    /**
     * Returns the Id of the experiment
     * @return
     *  The Id
     */
    public String getExpId(){
        return experiment.getId();
    }

    /**
     * Adds a binomial trial to the experiment
     * @param result
     */
    public void addBinomialTrial(Boolean result, GeoLocation location){
        TrialBinomial trial = new TrialBinomial(result);
        trial.setId(UUID.randomUUID().toString());
        trial.setProfile(profile);
        trial.setLocation(location);
        experiment.addTrial(trial);
        draftManager.addDraft(trial);
    }

    /**
     * Adds a non negative Integer count trial to the experiment
     * @param result
     */
    public void addNonNegIntTrial(Integer result, GeoLocation location){
        TrialIntCount trial = new TrialIntCount(result);
        trial.setId(UUID.randomUUID().toString());
        trial.setProfile(profile);
        trial.setLocation(location);
        experiment.addTrial(trial);
        draftManager.addDraft(trial);
    }

    /**
     * Adds a measurement trial to the experiment
     * @param result
     */
    public void addMeasurementTrial(Double result, GeoLocation location){
        TrialMeasurement trial = new TrialMeasurement(result);
        trial.setId(UUID.randomUUID().toString());
        trial.setProfile(profile);
        trial.setLocation(location);
        experiment.addTrial(trial);
        draftManager.addDraft(trial);
    }

    /**
     * Adds a count trial to the experiment
     */
    public void addCountTrial(GeoLocation location){
        TrialCount trial = new TrialCount(UUID.randomUUID().toString(),location,profile);
        experiment.addTrial(trial);
        draftManager.addDraft(trial);
    }

    /**
     * Returns the number of trials in the experiment before uploading
     * @return
     */
    public Integer getPreUploadedNTrials(){
        return originalNTrials;
    }

    /**
     * Returns number of trials in the experiment object(Unuploaded Trials included)
     * @return
     */
    public Integer getNTrials(){
        return Integer.parseInt(experiment.getNumTrials());
    }

    /**
     * Returns the minimum number of trials of the experiment
     * @return
     */
    public int getMinNTrials(){
        return experiment.getMinNTrials();
    }

    /**
     * Returns the title of the experiment
     * @return
     *  The title of the experiment
     */
    public String getTitle() {
        return experiment.getTitle();
    }

    /**
     * Returns if experiment is open
     * @return
     */
    public Boolean getOpen(){return experiment.getOpen();}
    /**
     * Returns the experiment type
     * @return
     */
    public String getType(){
        return experiment.getType();
    }

    /**
     * TO DO: Uploads the trials to firbase
     */
    public void uploadTrials(){
        firebaseManager.uploadTrials(experiment);
        this.originalNTrials=Integer.parseInt(experiment.getNumTrials());
        draftManager.deleteDrafts();
    }
    /**
     * Removes all trials inserted by the user from the experiment object
     */
    public void deleteTrials(){
        int elementsToRemove=(Integer.parseInt(experiment.getNumTrials()) - originalNTrials);
        for (int i=0;i<elementsToRemove;i++){
            experiment.delTrial(experiment.getAllTrials().size()-1);
        }
        draftManager.deleteDrafts();
    }

    /**
     * Created a QrCode object to be uploaded to the database for later use
     * @param value
     *  the value attached to the trial, for non-negatice integer counts and measurements, it is
     *  a value given by the user, for binomials it is 1 for a pass, 0 for a fail, for counts,
     *  it is 1 for increment, -1 for commit
     * @return
     *  returns the QrCode object
     */
    public QrCode createQrCodeObject(double value){
        String id = UUID.randomUUID().toString();
        QrCode newQR = new QrCode(id, experiment.getId(), experiment.getType(), value);
        return newQR;
    }

    /**
     * Uploads a QrCode object to the Firestore database
     * @param qr
     *  The QrCode object to be uploaded
     */
    public void uploadQR(QrCode qr){
        Map<String, Object> map = new HashMap<>();
        map.put("Id", qr.getQrId());
        map.put("ExperimentId", qr.getExperimentId());
        map.put("TrialType", qr.getTrialType());
        map.put("Value", qr.getValue());
        firebaseManager.set("qrCodeData", qr.getQrId(), map);
    }

    /**
     * Generates a Bitmap QrCode corresponding to the given QrCode ID
     * adapted from the following stack overflow question answer from user user6017633 on April 7th 2017
     * https://stackoverflow.com/a/43284184
     * @param id
     *  The QrCode Id to be encoded
     * @return
     *  The bitmap of the QrCode
     * @throws WriterException
     */
    public Bitmap IdToQrCode(String id) throws WriterException{
        BitMatrix bitMatrix;
        int size = 400;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    id,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    size, size
            );
        } catch (IllegalArgumentException illegalArgumentException){
            Log.d("Encode", illegalArgumentException.getMessage());
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        int colorBlack = Color.BLACK;
        int colorWhite = Color.WHITE;

        for(int i = 0; i < bitMatrixHeight; i++) {
            int offset = i * bitMatrixWidth;

            for (int j = 0; j < bitMatrixWidth; j++){
                pixels[offset + j] = bitMatrix.get(i, j) ? colorBlack:colorWhite;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 400, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    /**
     * Returns the distance in metres between the center of the region and a point on the Earth's
     * surface (as a latitude/longitude pair).
     * @param point
     *      The point whose distance from the center of the region we want to calculate.
     * @return
     *      The distance, in metres, between the center of the region and the point.
     */
    private double calculateDistance(GeoLocation point) {
        // The haversine formula is used here.
        // All distances are in metres. All angles are in radians.

        double cLat = experiment.getRegion().getLat();
        double cLon = experiment.getRegion().getLon();
        double pLat = point.getLat();
        double pLon = point.getLon();

        final double R = 6371000; // Mean Earth's radius

        // Converting angles to radians, and computing intermediate values
        final double phi1 = cLat * Math.PI/180;
        final double phi2 = pLat * Math.PI/180;
        final double deltaPhi = (pLat - cLat) * Math.PI/180;
        final double deltaLambda = (pLon - cLon) * Math.PI/180;

        // Angular distance in radians between the two points
        final double a = Math.pow(Math.sin(deltaPhi/2), 2) + (Math.cos(phi1) * Math.cos(phi2)
                * Math.pow(Math.sin(deltaLambda/2), 2));
        // Square of half the chord length between the two points
        final double c = Math.atan2(Math.sqrt(a), Math.sqrt(1-a)) * 2;

        final double distance = R*c; // Distance between the two points

        return distance;
    }

    /**
     * Checks if the experiment is such that trials are enforced to be withing region.
     * @return
     */
    public boolean isLocationEnforced() {
        return experiment.getReqLocation();
    }

    /**
     * Checks if a point on the Earth's surface is within the region of an experiment. Only meant
     * to be called for experiments that have a region set, i.e. distance between the center of the
     * region and the point is less than the radius of the region.
     * @param point
     *      The point whose existence within the region we want to check.
     * @return
     *      true if the point is within the region, false otherwise.
     */
    public boolean isWithinRegion(GeoLocation point) {
        return experiment.getRegion().getRadius() >= calculateDistance(point);
    }

}
