package com.example.sparktrials.models;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * An experiment class that has an owner, multiple specs that define the experiment, and trials
 * owner is the user who created the experiment
 * trials is the array of trials that have been committed to the experiment
 * title is the title of the experiment
 * desc is the description of the experiment
 * region is the location that the experiment is held at
 * minNTrials is the minimum number of trials an experimenter must complete before committing
 * open is a boolean value, that states whether the experiment is accepting new trials or not
 */
public class Experiment {
    private String id;
    private String type;
    private Profile owner;
    private ArrayList<Trial> trials;
    private String title;
    private String desc;
    private GeoLocation region;
    private Boolean reqLocation;
    private Integer minNTrials;
    private Boolean open;
    private Boolean published;
    private Date date;
    private ArrayList<String> blacklist;

    private Double upperBound;
    private Double lowerBound;

    /**
     * Initiates an empty experiment that will be filled out later
     * Sets experiment attribute to some default values
     */
    public Experiment(String id){
        this.id = id;
        this.type = null;
        this.owner = null;
        this.trials = new ArrayList<>();
        this.title = "N/A";
        this.desc = "N/A";
        this.region = null;
        this.reqLocation = false;
        this.minNTrials = 0;
        this.open = true;
        this.published = true;
        this.date = new Date();
        this.blacklist = new ArrayList<>();
    }

    /**
     * Initiates a new experiment with the given values
     * USED FOR CREATING NEW EXPERIMENTS
     * @param owner
     *    The user who created this experiment
     * @param title
     *    The title of the experiment (name)
     * @param desc
     *    The description for the experiment
     * @param region
     *    The location that the experiment is being held at
     * @param minNTrials
     *    The minimum number of trials that a user has to commit before their trials are counted
     */
    public Experiment(String id, String type, Profile owner, String title, String desc, GeoLocation region, Boolean reqLocation, Integer minNTrials){
        this.id = id;
        this.type = type.toLowerCase();
        this.owner = owner;
        this.trials = new ArrayList<>();
        this.title = title;
        this.desc = desc;
        this.region = region;
        this.reqLocation = reqLocation;
        this.minNTrials = minNTrials;
        this.open = true;
        this.published = true;
        this.date = new Date();
        this.blacklist = new ArrayList<>();
    }

    /**
     * Recreates an experiment
     * ONLY USE WHEN DOWNLOADING A PRE-EXISTING EXPERIMENT
     * @param owner
     *    The user who created this experiment
     * @param trials
     *    A list of all the trials in the experiment
     * @param title
     *    The title of th experiment (name)
     * @param desc
     *    The description for the experiment
     * @param region
     *    The location that the experiment is being held at
     * @param minNTrials
     *    The minimum number of trials that a user has to commit before their trials are counted
     * @param open
     *    The boolean value of whether the experiment is "open" for more trials or not
     * @param date
     *    The date that the experiment was created on
     */
    public Experiment(String id, String type, Profile owner, ArrayList<Trial> trials, String title, String desc, GeoLocation region,
                      Boolean reqLocation, Integer minNTrials, Boolean open, Boolean published, Date date, ArrayList<String> blacklist){
        this.id = id;
        this.type = type.toLowerCase();
        this.owner = owner;
        this.trials = trials;
        this.title = title;
        this.desc = desc;
        this.region = region;
        this.reqLocation = reqLocation;
        this.minNTrials = minNTrials;
        this.open = open;
        this.published = published;
        this.date = date;
        this.blacklist = blacklist;
    }

    public Experiment() {
        this.date = new Date();

    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Fetches the id of the experiment
     * @return
     *    The integer id that the experiment holds
     */
    public String getId() {
        return id;
    }

    /**
     * Fetches the type of the experiment
     * @return
     *    The type of the experiment as a string.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the experiment to a new value
     * SHOULD NOT BE USED UNLESS CHANGING FROM NULL!
     * @param type
     *    The type that the experiment will be
     *    Should be either binomial, count, intergcount, or measure
     */
    public void setType(String type) {
        if (this.type==null) {
            this.type = type.toLowerCase();
        }
    }

    /**
     * Fetches every trial that's ever been uploaded to the experiment
     * @return
     *    Returns an array list of all the trials in the experiment
     */
    public ArrayList<Trial> getAllTrials() {
        return trials;
    }

    /**
     * Replaces the list of trials with a new list of trials
     * Should ONLY be used to initialize an experiment, NEVER to replace trials
     * @param trials
     */
    public void setTrials(ArrayList<Trial> trials){
        if (this.trials.size() == 0) {
            this.trials = trials;
        }
    }

    /**
     * For each experimenter that has submitted more than the minimum number of trials,
     * retrieve their trials
     * @return
     *    returns a list of "valid" trials
     */
    public ArrayList<Trial> getValidTrials() {
        ArrayList<Trial> valid_trials = new ArrayList<>();
        //create a hash map that, for each user, will have an array list populated with
        //that users trials
        HashMap<String, ArrayList<Trial>> user_trials = new HashMap<>();
        Trial trial;
        String user_id;
        for (int i=0; i < this.trials.size(); i++){
            trial = this.trials.get(i);
            if (this.blacklist.contains(trial.getProfile().getId())){
                continue;
            }
            user_id = trial.getProfile().getId();
            if (user_trials.get(user_id) == null){
                ArrayList<Trial> arr_list = new ArrayList<>();
                user_trials.put(user_id, arr_list);
            }

            user_trials.get(user_id).add(trial);
        }

        //for each user in the hash map, Add their trials to the list of valid trials
        for (String i : user_trials.keySet()){
            ArrayList<Trial> trials_of_user = user_trials.get(i);
            for (int j=0; j<trials_of_user.size(); j++){
                valid_trials.add(trials_of_user.get(j));
            }
        }

        return valid_trials;
    }

    /**
     * Gets a specific trial, if not found, return null
     * @param id
     *    Takes in an id of a trial to find it
     * @return
     *    Returns the requested trial, or a null value
     */
    public Trial getTrial(String id) {
        for (int i=0; i < this.trials.size(); i++){
            if (this.trials.get(i).getId().equals(id)){
                return this.trials.get(i);
            }
        }

        return null;
    }

    /**
     * Uploads a trial to the experiment
     * @param trial
     *    The trial to be added to the experiment
     */
    public void addTrial(Trial trial){
        if (this.open){
            this.trials.add(trial);
        }
    }

    /**
     * Deletes a trial from the trial list
     * @param trial
     *    The trial to delete
     */
    public void delTrial(Trial trial){
        this.trials.remove(trial);
    }

    /**
     * Returns whether the experiment is published or not
     * @return
     *  true if the experiment is published, false if not
     */
    public Boolean getPublished() {

        if (published != null){
            return published;
        } else {
            return true;
        }
    }

    /**
     * Update the value of the published boolean
     * @param publication
     *  the new value
     */
    public void setPublished(Boolean publication) {
        this.published = publication;
    }

    /**
     * Deletes a trial from the trial list
     * @param id
     *    The id of the trial to delete
     */
    public void delTrial(Integer id){
        Trial trial = this.trials.get(id);
        this.delTrial(trial);

    }

    /**
     * Retrieves every single trial from a specified user
     * @param id
     *    The id of the user that you want the trials from
     * @return
     *    Returns a list of trials, where each trials is from the specified user
     */
    public ArrayList<Trial> getUserTrials(String id){
        ArrayList<Trial> chosenTrials = new ArrayList<>();
        for (int i=0; i < this.trials.size(); i++){
            if (this.trials.get(i).getProfile().getId().equals(id)){
                chosenTrials.add(this.trials.get(i));
            }
        }
        return chosenTrials;
    }
    /**
     * Uploads an entire list of trials into the experiment
     * @param trials
     *   The array list containing the trials desired to be uploaded
     */
    public void addTrials(ArrayList<Trial> trials) {
        if (this.open) {
            for (int i = 0; i < trials.size(); i++) {
                this.trials.add(trials.get(i));
            }
        }
    }

    /**
     * Gets the title of the experiment
     * @return
     *    returns a string of the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the experiment
     * @param title
     *    A string containing the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the experiment
     * @return
     *    Returns the string containing the experiment's description
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the description of the experiment
     * @param desc
     *    A string containing the new description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the region (geo-location) of the experiment
     * @return
     *    Returns the GeoLocation of the experiment
     */
    public GeoLocation getRegion() {
        return region;
    }

    /**
     * Sets the region of the experiment
     * @param region
     *    The new region for the experiment
     */
    public void setRegion(GeoLocation region) {
        this.region = region;
    }

    /**
     * Checks if the experiment has a location set.
     * @return
     *      true if the experiment has a location set, false otherwise
     */
    public boolean hasLocationSet() {
        return (getRegion().getRadius() > 0);
    }

    /**
     * Gets whether the experiment requires locations or not
     * @return
     *    Returns a boolean true=requires locations, false=doesn't require locations
     */
    public Boolean getReqLocation() {
        return reqLocation;
    }

    /**
     * Sets whether the experiment is required or not
     * @param reqLocation
     *    A boolean true/false
     */
    public void setReqLocation(Boolean reqLocation) {
        this.reqLocation = reqLocation;
    }

    /**
     * Gets the minimum number of trials needed for a user's trials to get counted
     * @return
     *    Returns an integer for the number of trials needed
     */
    public Integer getMinNTrials() {
        return minNTrials;
    }

    /**
     * Sets the minimum number of trials needed for a user's trials to get counted
     * @param minNTrials
     *    The new minimum
     */
    public void setMinNTrials(Integer minNTrials) {
        this.minNTrials = minNTrials;
    }

    /**
     * Gets the owner of the experiment
     * @return
     */
    public Profile getOwner() {
        return owner;
    }

    /**
     * Sets a new owner for the experiment. You really should not use this to *change* the owner
     * @param owner
     */
    public void setOwner(Profile owner) {
        this.owner = owner;
    }

    /**
     * Gets whether the experiment is open for new trials or not
     * @return
     *    Returns a boolean - true=open, false=closed
     */
    public Boolean getOpen() {
        return open;
    }

    /**
     * Changes the open status of the experiment
     * @param open
     */
    public void setOpen(Boolean open) {
        this.open = open;
    }

    /**
     * Gets the start date of the experiment
     * @return
     *    Returns the date that the experiment started
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Gets the sart date of the experiment formatted as a string
     * @return
     *    Returns the string of the date
     */
    public String getDay(Date date){
        String pattern = "EEE MMM dd HH:mm:ss z yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        String strDate = df.format(date);
        strDate = strDate.substring(4,10);

        return strDate;
    }
    /**
     * Sets a new start date of the experiment. You really should not use this to *change* the date
     * @param date
     *    The new start date that the experiment will take
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This method gets the blacklist that a profile has
     * @return
     *    A list containing the ids of every experiment that the user is subscribed to
     */
    public ArrayList<String> getBlacklist() {
        return this.blacklist;
    }

    /**
     * Replace the blacklist list
     * Should not really be used except to create a Profile
     * @param blacklist
     *    A list of profile ids
     */
    public void setBlacklist(ArrayList<String> blacklist) {
        this.blacklist = blacklist;
    }

    /**
     * Adds an profile id to the list of blacklist
     * Only add it if it doesn't already exist inside it
     * @param proId
     *    And profile's id
     */
    public void addToBlacklist(String proId) {
        if (!this.isBlacklisted(proId)) {
            this.blacklist.add(proId);
        }
    }

    /**
     * Delete an profile id in the list of blacklist
     * @param proId
     *    The profile id to delete
     */
    public void delFromBlacklist(String proId) {
        this.blacklist.remove(proId);
    }

    /**
     * Adds a list of profile ids to the list of blacklist
     * @param proIds
     *    A list of profile ids
     */
    public void addManyBlacklist(ArrayList<String> proIds){
        for (int i=0; i<proIds.size(); i++){
            this.addToBlacklist(proIds.get(i));
        }
    }

    /**
     * Delete a group of profile ids from the last of blacklist
     * @param proIds
     *   A list of profile ids
     */
    public void delManyBlacklist(ArrayList<String> proIds){
        this.blacklist.removeAll(proIds);
    }

    /**
     * Checks if an profile's id is in this profile's subscribed profiles
     * @param proId
     *    The profile id to check for
     * @return
     *    returns true if profile id is in blacklist
     */
    public boolean isBlacklisted(String proId) {
        return this.blacklist.contains(proId);
    }

    /**
     * Sorts the trials of an experiment in ascending order this comes in handy for calculating descriptive stats
     * @return
     * A sorted list of trials
     */
    public ArrayList<Double> trialsValuesSorted(){
        ArrayList <Double>  values = new ArrayList<>();
        for(int i = 0; i<getValidTrials().size(); i++){
            values.add( (Double) ( getValidTrials().get(i).getValue()));
        }
        Collections.sort(values);

        return values;
    }

    /**
     * Determines all the days trials occurs
     * @return
     * Array List of type string
     */
    public ArrayList<String> daysOfTrials(){
        ArrayList<String> days = new ArrayList<>();
        for (int i = 0 ; i < getValidTrials().size(); i++){
            Date date = getValidTrials().get(i).getDate();
            String day = getDay(date);
            if (!days.contains(day)){
                 days.add(day);
            }
        }
        return  days;
    }

    /**
     * Removes duplicate values of trials necessary for calculating frequencies at which trials occur
     * @return
     * A sorted list with no duplicates
     */
    public Double[] removeDupes(){
        Double[] cleanArray ;
        LinkedHashSet <Double> noDupes = new LinkedHashSet<Double>(trialsValuesSorted());
        cleanArray = new Double[noDupes.size()];
        noDupes.toArray(cleanArray);
        return cleanArray;

    }

    /**
     * Calculates the x-axis values for the histogram/plot in the stats tab
     * @return
     * A sorted list of type string
     */
    public String [] getXaxis(){
        int maxBins = 15;
        Double[] dupesRemoved = removeDupes();
        int size = dupesRemoved.length;
        if (size < maxBins) {
            String[] str = new String[size];
            for (int i = 0; i < size; i++) {
                str[i] = dupesRemoved[i].toString();
            }
            return str;
        } else {
            ArrayList<Double> sortedValues = trialsValuesSorted();
            Double q1 = Double.parseDouble(getQ1());
            Double q3 = Double.parseDouble(getQ3());
            Double iq = q3-q1;
            Double boundMultiplier = 1.5;
            upperBound = sortedValues.get(0);
            lowerBound = sortedValues.get(0);
            for (int i=1; i<sortedValues.size(); i++) {
                Double value = sortedValues.get(i);
                if (value > upperBound) {
                    upperBound = value;
                }
                if (value < lowerBound) {
                    lowerBound = value;
                }
            }
            upperBound = Math.min(upperBound, q3+boundMultiplier*iq);
            lowerBound = Math.max(lowerBound, q1-boundMultiplier*iq);
            Double binSize = (upperBound-lowerBound)/maxBins;

            String[] str = new String[maxBins];
            for (double i = 0.0; i < maxBins; i++) {
                str[(int)i] = String.format("%.2f",lowerBound+i*binSize);
            }

            return str;
        }

    }

    /**
     * Calculates the so called frequencies or Y values for the histogram
     * @return
     * An integer list of frequencies which matches the the index of the getXAxis list
     */
    public int [] frequencies(){
        int maxBins = 15;
        String[] xAxis = getXaxis();
        ArrayList<Double> sortedValues = trialsValuesSorted();

        if (xAxis.length<maxBins) {
            HashMap<String, Integer> map = new HashMap<>();
            for (int i=0; i<sortedValues.size(); i++) {
                String value = sortedValues.get(i).toString();
                if (map.containsKey(value)) {
                    map.put(value, map.get(value) + 1);
                } else {
                    map.put(value, 1);
                }
            }

            int[] frequencies = new int[xAxis.length];
            for (int i = 0; i < xAxis.length; i++) {
                frequencies[i] = map.get(xAxis[i]);
            }
            return frequencies;


        } else {
            Double binSize = (upperBound-lowerBound)/maxBins;

            int[] frequencies = new int[maxBins];
            for (int i = 0; i < sortedValues.size(); i++) {
                Double value = sortedValues.get(i);
                double binNumber = -(lowerBound-value)/binSize;
                if (binNumber >= 0 && binNumber < maxBins) {
                    frequencies[(int)binNumber] += 1;
                } else if (binNumber == maxBins) {
                    binNumber = maxBins-1;
                    frequencies[(int)binNumber] += 1;
                }
            }

            return frequencies;
        }
    }

    /**
     * Calculates desired frequencies for days useful for line plots
     * @return
     * frequencies of type double
     */
    public double [] daysFrequencies(){
        double []frequencies = new double [daysOfTrials().size()];
        int numDays = daysOfTrials().size();
        if (this.getType().equals("binomial trials")){
            double success = 0;
            double failure = 1 ;
            for (int i = 0 ; i < numDays ; i++){
                String day = daysOfTrials().get(i);
                double proportionOfSuccess = 0;
                for (int j = 0; j< getValidTrials().size(); j++){
                    String dayOfThisTrial = getDay(getValidTrials().get(j).getDate());
                    if (day.equals(dayOfThisTrial)){
                        if(getValidTrials().get(j).getValue() == 0){
                            success +=1;
                        }
                        else {
                            failure+=1;
                        }
                    }
                }
                proportionOfSuccess = success/failure;
                frequencies[i]+=proportionOfSuccess;
            }
            return frequencies;
        }
        if (this.getType().equals("counts")){
            for (int i = 0 ; i < numDays ; i++){
                String day = daysOfTrials().get(i);
                for (int j = 0; j< getValidTrials().size(); j++){
                    String dayOfThisTrial = getDay(getValidTrials().get(j).getDate());
                    if (day.equals(dayOfThisTrial)){
                        frequencies[i]+=getValidTrials().get(j).getValue();
                    }
                }
            }
            return frequencies;
        }
        if (this.getType().equals("non-negative integer counts")){
            for (int i = 0 ; i < daysOfTrials().size() ; i++){
                double count = 0;
                double sum = 0;
                String day = daysOfTrials().get(i);
                for (int j = 0; j< getValidTrials().size(); j++){
                    String dayOfThisTrial = getDay(getValidTrials().get(j).getDate());
                    if (day.equals(dayOfThisTrial)){
                        sum+=getValidTrials().get(j).getValue();
                        count +=1;
                    }
                }
                frequencies[i]= sum/count;
            }
            return frequencies;
        }
        if (this.getType().equals("measurement trials")){
            for (int i = 0 ; i < numDays ; i++){
                double count = 0;
                double sum = 0;
                String day = daysOfTrials().get(i);
                for (int j = 0; j< getValidTrials().size(); j++){
                    String dayOfThisTrial = getDay(getValidTrials().get(j).getDate());
                    if (day.equals(dayOfThisTrial)){
                        sum+=getValidTrials().get(j).getValue();
                        count +=1;
                    }
                }
                frequencies[i]= sum/count;
            }
            return frequencies;
        }
        return frequencies;
    }

    /**
     * Calculates q1 data for line plots over days
     * @return
     * Q1 points over days of type double
     */
    public double [] q1Plots(){
        double []frequencies = new double [daysOfTrials().size()];
        ArrayList<String> days = daysOfTrials();
        if (trialsValuesSorted().isEmpty()) {
            frequencies[0] = 0;
            return frequencies;
        }
        int numDays = days.size();
        for (int i=0; i<numDays;i++){
            ArrayList<Double> thisDaysValues = new ArrayList<>();
            for (int j=0;j<getValidTrials().size();j++){
                String day = getDay(getValidTrials().get(j).getDate());
                if (day.equals(days.get(i))){
                    thisDaysValues.add(getValidTrials().get(j).getValue());
                }
            }
            Collections.sort(thisDaysValues);
            double quartile;
            if (thisDaysValues.size() <= 3){
                quartile = thisDaysValues.get(0);
            } else {
                int length = thisDaysValues.size();
                float newArraySize = (length * ((float) (1) * 25 / 100));
                if (newArraySize % 1 == 0) {
                    quartile = thisDaysValues.get((int) newArraySize);
                } else {
                    int newArraySize1 = (int) (newArraySize);
                    quartile = (thisDaysValues.get(newArraySize1) + thisDaysValues.get(newArraySize1 + 1)) / 2.0;
                }
            }
            frequencies[i] = quartile;

        }
        return frequencies;
    }

    /**
     * Calculates Q3 plots for graphing over days
     * @return
     * q3 plots of type double
     */
    public double [] q3Plots(){
        double []frequencies = new double [daysOfTrials().size()];
        ArrayList<String> days = daysOfTrials();

        int numDays = days.size();
        for (int i=0; i<numDays;i++){
            ArrayList<Double> thisDaysValues = new ArrayList<>();
            for (int j=0;j<getValidTrials().size();j++){
                String day = getDay(getValidTrials().get(j).getDate());
                if (day.equals(days.get(i))){
                    thisDaysValues.add(getValidTrials().get(j).getValue());
                }
            }
            Collections.sort(thisDaysValues);
            double quartile;
            if (thisDaysValues.size() <= 3){
                quartile = thisDaysValues.get(0);
            } else {
                int length = thisDaysValues.size();
                float newArraySize = (length * ((float) (3) * 25 / 100));
                if (newArraySize % 1 == 0) {
                    quartile = thisDaysValues.get((int) newArraySize);
                } else {
                    int newArraySize1 = (int) (newArraySize);
                    quartile = (thisDaysValues.get(newArraySize1) + thisDaysValues.get(newArraySize1 + 1)) / 2.0;
                }
            }
            frequencies[i] = quartile;

        }
        Log.d("q3", ""+ frequencies[0]);
        return frequencies;

    }
    /**
     * Calculates the Median value for the experiment
     * If even number of numbers, then take average of middle two
     * @return
     * Median of type string
     */
    public String getMedian(){
        double median =0;
        if (trialsValuesSorted().isEmpty() ){
            return "N/A";
        }
        int num = trialsValuesSorted().size();
        if (num  % 2 == 0){
            median = ((double)(trialsValuesSorted().get(num/2) + trialsValuesSorted().get(num/2 - 1)))/2.0;
        }else{
            median = ((double) trialsValuesSorted().get(num/2));
        }
        return ""+ median;
    }

    /**
     * Calculates the Q1 (quartile 1) for the experiment
     * @return
     * Q1 of type string
     */
    public String getQ1(){
        ArrayList<Double> trials = trialsValuesSorted();

        double quartile;
        if (trials.isEmpty() ){
            return "N/A";
        }

        if (trials.size() <= 3){
            quartile = trials.get(0);
        } else {
            int length = trials.size();
            float newArraySize = (length * ((float) (1) * 25 / 100));
            if (newArraySize % 1 == 0) {
                quartile = trials.get((int) newArraySize);
            } else {
                int newArraySize1 = (int) (newArraySize);
                quartile = (trials.get(newArraySize1) + trials.get(newArraySize1 + 1)) / 2.0;
            }
        }
        return String.format("%.2f", quartile);
    }

    /**
     * Calculates the Q3 (quartile 3) for the experiment
     * @return
     * Q3 of type string
     */
    public String getQ3(){
        ArrayList<Double> trials = trialsValuesSorted();

        double quartile;
        if (trials.isEmpty() ){
            return "N/A";
        }

        if (trials.size() <= 3){
            quartile = trials.get(trials.size() - 1);
        } else {
            int length = trials.size();
            float newArraySize = (length * ((float) (3) * 25 / 100))-1;
            if (newArraySize % 1 == 0) {
                quartile = trials.get((int) newArraySize);
            } else {
                int newArraySize1 = (int) (newArraySize);
                quartile = (trials.get(newArraySize1) + trials.get(newArraySize1 + 1)) / 2.0;
            }
        }
        return String.format("%.2f", quartile);
    }

    /**
     * Finds the total number of trials in an experiment
     * @return
     * Number of trials of type string
     */
    public String getNumTrials(){
        return "" +getValidTrials().size();
    }

    /**
     * Calculates the standard deviation for the experiment
     * @return
     * Standard deviation of type string
     */
    public String getStd(){
        if (trialsValuesSorted().isEmpty() ){
            return "N/A";
        }
        int sum =0 ;
        double mean;
        int num = getValidTrials().size();
        double std= 0;
        for (int i=0 ; i<num;  i++){
            sum+= getValidTrials().get(i).getValue();
        }
        mean = ((double) sum) / ((double) num);
        for (int i = 0 ; i<num; i++){
            std+= Math.pow(getValidTrials().get(i).getValue()- mean, 2);
        }
        std = Math.sqrt(std/num);
        return String.format("%.2f", std);
    }

    /**
     * Calculates the mean (average) for an experiment
     * @return
     * Mean of type String
     */
    public String getMean(){
        if (trialsValuesSorted().isEmpty() ){
            return "N/A";
        }
        double sum =0 ;
        double mean;
        int num = getValidTrials().size();
        for (int i=0 ; i<num;  i++){
            sum+= getValidTrials().get(i).getValue();
        }
        mean = sum / ((double) num);
        return String.format("%.2f", mean);
    }

    /**
     * Determines necessary header for histogram graph
     * @return
     * Spannable header (underlined) for histogram
     */
    public SpannableString getHistogramHeader(){
        String header = "";
        if (this.getType().equals("binomial trials")){
            header = "Proportion of Pass/Fail";
        }
        if (this.getType().equals("counts")){
            header = "Total Counts";
        }
        if (this.getType().equals("non-negative integer counts")){
            header = "Total Counts per Count";
        }
        if (this.getType().equals("measurement trials")){
            header = "Total Measurements per Measurement";
        }
        SpannableString spannable_header = new SpannableString(header);
        spannable_header.setSpan(new UnderlineSpan(), 0 , header.length(), 0);
        return spannable_header;
    }

    /**
     * Determines header for plots
     * @return
     * Spannable header for plots
     */
    public SpannableString getPlotHeader(){
        String header = "";
        if (this.getType().equals("binomial trials")){
            header = "Daily Success Rates";
        }
        if (this.getType().equals("counts")){
            header = "Total Daily Counts";
        }
        if (this.getType().equals("non-negative integer counts")){
            header = "Average Daily Counts";
        }
        if (this.getType().equals("measurement trials")){
            header = "Average Daily Measurements";
        }
        SpannableString spannable_header = new SpannableString(header);
        spannable_header.setSpan(new UnderlineSpan(), 0 , header.length(), 0);
        return spannable_header;
    }
}
