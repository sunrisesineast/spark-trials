package com.example.sparktrials.models;

/**
 * This class represents what a QrCode needs in order to add a trial to an experiment
 * the actual QR code will decode to the ID of the QR code and the app will pull the
 * rest of the information from the database based on that ID
 */

public class QrCode {
    private String id;
    private String experimentId;
    private String trialType;
    private double value;

    /**
     * This constructor initializes the parameters needed for the QrCode to add a trial.
     * @param id
     *  The id of the QrCode
     * @param experimentId
     *  The id of the experiment the trial is for
     * @param trialType
     *  The type of trial the experiment requires
     * @param value
     *  The value of the trail connected to this QrCode
     */
    public QrCode(String id, String experimentId, String trialType, double value){
        this.id = id;
        this.experimentId = experimentId;
        this.trialType = trialType;
        this.value = value;
    }

    /**
     * Gets the Id of the QrCode
     * @return
     *  Returns the id if the QrCode
     */
    public String getQrId() {
        return id;
    }

    /**
     * Sets the id of the QrCode
     * @param id
     *  The id to set it to
     */
    public void setQrId(String id) {
        this.id = id;
    }

    /**
     * Gets the id of the corresponding experiment
     * @return
     *  Returns the id of the experiment
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * Sets the experiment ID of the corresponding experiment
     * @param experimentId
     *  The ID to set as the experiment ID
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * Get the type of trial this QrCode is adding
     * @return
     *  Returns the type of trial
     */
    public String getTrialType() {
        return trialType;
    }

    /**
     * Sets the type of trial being added by the QrCode
     * @param trialType
     *  The trial type to be set
     */
    public void setTrialType(String trialType) {
        this.trialType = trialType;
    }

    /**
     * Gets the value of the trial being added by this code
     * @return
     *  Returns the value trials from this QrCode will have
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of trials added by this QrCode
     * @param value
     *  the value we want this QrCode to give it's trials
     */
    public void setValue(double value) {
        this.value = value;
    }
}
