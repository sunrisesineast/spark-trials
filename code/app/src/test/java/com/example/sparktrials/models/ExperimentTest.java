package com.example.sparktrials.models;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

/**
 * A class to test every single method of the Experiment class
 */
public class ExperimentTest extends TestCase {

    Experiment exp;

    /**
     * Verifies that the id retrieved is the same as the id that the class holds
     */
    public void testGetId(){
        exp = new Experiment("10");
        assertEquals("getId does not work", "10", exp.getId());
    }

    /**
     * Verifies that the trials retrieved are the trials that the experiment actually has
     * Assumes setTrials works
     */
    public void testGetAllTrials() {
        exp = new Experiment("10");
        ArrayList<Trial> toSet = this.createTrials();
        exp.setTrials(toSet);
        assertEquals("getAllTrials returns wrong amount", 15, exp.getAllTrials().size());
        assertEquals("getAllTrials returns wrong trial", "0", exp.getAllTrials().get(0).getId());
        assertEquals("getAllTrials returns wrong trial", "14", exp.getAllTrials().get(exp.getAllTrials().size()-1).getId());

    }

    /**
     * Verifies that the trials retrieved are the trials whose submitter has submitted
     * sufficiently many trials
     * Assumes setTrials and setMinNTrials works
     */
    public void testGetValidTrials() {
        exp = new Experiment("10");
        exp.setMinNTrials(5);
        ArrayList<Trial> toSet = this.createTrials();
        exp.setTrials(toSet);
        assertEquals("getValidTrials returns wrong amount", 15, exp.getValidTrials().size());
        assertEquals("getValidTrials returns wrong trial", "4", exp.getValidTrials().get(0).getId());
        assertEquals("getValidTrials returns wrong trial", "14", exp.getAllTrials().get(exp.getAllTrials().size()-1).getId());
    }

    /**
     * Tests the get trial method by verifying that the trials it retrieves are the ones that the experiment holds
     * Assumes setTrials works
     */
    public void testGetTrial() {
        exp = new Experiment("10");
        ArrayList<Trial> toSet = this.createTrials();
        exp.setTrials(toSet);
        assertEquals("getTrial incorrect", "id1", exp.getTrial("1").getProfile().getId());
        assertEquals("getTrial incorrect", "id2", exp.getTrial("5").getProfile().getId());
        assertEquals("getTrial incorrect", "id3", exp.getTrial("11").getProfile().getId());
        assertEquals("getTrial incorrect", null, exp.getTrial("-1"));
    }

    /**
     * Tests the add trial method by adding a trial and then verifying that the experiment
     * now holds the trial that was submitted
     * Assumes getTrial works
     */
    public void testAddTrial() {
        exp = new Experiment("10");
        TrialCount trial = new TrialCount("109", new GeoLocation(0.0, 0.0), new Profile());
        exp.addTrial(trial);
        assertEquals("addTrial does not work", "109", exp.getTrial("109").getId());
        assertEquals("addTrial does not work", null, exp.getTrial("109").getProfile().getId());
    }

    /**
     * Tests the getUserTrials method by checking if the trials returned given a profile id are all the trials
     * that that profile submitted
     */
    public void testGetUserTrials() {
        exp = new Experiment("10");
        ArrayList<Trial> toSet = this.createTrials();
        exp.setTrials(toSet);
        assertEquals("getUserTrials does not work", "0", exp.getUserTrials("id1").get(0).getId());
        assertEquals("getUserTrials does not work", "9", exp.getUserTrials("id2").get(exp.getUserTrials("id2").size()-1).getId());
    }

    /**
     * Tests the addTrials methods by checking if the added trials match those given to the method
     * Assumes that getTrial works
     */
    public void testAddTrials() {
        exp = new Experiment("10");
        TrialCount trial1 = new TrialCount("109", new GeoLocation(0.0, 0.0), new Profile());
        TrialCount trial2 = new TrialCount("110", new GeoLocation(1.0, 1.0), new Profile());
        ArrayList<Trial> trials = new ArrayList<>();
        trials.add(trial1);
        trials.add(trial2);
        exp.addTrials(trials);

        assertEquals("addTrials does not work", "109", exp.getTrial("109").getId());
        assertEquals("AddTrials does not work", "110", exp.getTrial("110").getId());
    }

    /**
     * Tests both the getTitle and setTitle methods by setting the title and then
     * checking that the set title is what is returned by the getTitle method
     */
    public void testGetSetTitle() {
        exp = new Experiment("10");
        exp.setTitle("test");
        assertEquals("test/set title does not work", "test", exp.getTitle());
    }

    /**
     * Tests both the getDesc and setDesc methods by setting the desc and then
     * checking that the set desc is what is returned by the getDesc method
     */
    public void testGetSetDesc() {
        exp = new Experiment("10");
        exp.setDesc("test");
        assertEquals("get/setDesc does not work", "test", exp.getDesc());
    }

    /**
     * Tests both the getRegion and setRegion methods by setting the region and then
     * checking that the set region is what is returned by the getRegion method
     */
    public void testGetSetRegion() {
        exp = new Experiment("10");
        GeoLocation location = new GeoLocation(50.0, 40.0);
        exp.setRegion(location);
        assertEquals("get/setRegion does not work", 50.0, exp.getRegion().getLat());
        assertEquals("get/setRegion does not work", 40.0, exp.getRegion().getLon());
    }

    /**
     * Tests both the getMinNTrials and setMinNTrials methods by setting the minNTrials and then
     * checking that the set minNTrials is what is returned by the getMinNTrials method
     */
    public void testGetSetMinNTrials() {
        exp = new Experiment("10");
        exp.setMinNTrials(5);
        assertEquals("get/setMinNTrials does not work", 5, (int)exp.getMinNTrials());
    }

    /**
     * Tests both the getOwner and setOwner methods by setting the owner and then
     * checking that the set owner is what is returned by the getOwner method
     */
    public void testGetSetOwner() {
        exp = new Experiment("10");
        Profile owner = new Profile("id1", "owner", "contact");
        exp.setOwner(owner);
        assertEquals("get/setOwner does not work", "owner", exp.getOwner().getUsername());
    }

    /**
     * Tests both the getOpen and setOpen methods by setting open to false and then
     * checking that open is now returning false
     */
    public void testGetSetOpen() {
        exp = new Experiment("10");
        exp.setOpen(false);
        assertEquals("get/setOpen does not work", false, (boolean)exp.getOpen());
    }

    /**
     * Tests both the getDate and setDate methods by setting the date and then
     * checking that the set date is what is returned by the getOwner method
     */
    public void testGetSetDate() {
        exp = new Experiment("10");
        Date date = new Date();
        exp.setDate(date);
        assertEquals("get/setDate does not work", date, exp.getDate());
    }

    /**
     * NOT A TEST
     * Creates 15 trials, where their id is the number in which they were created
     * First 4 trials have profile id id1, next 6 have id2, and final 5 have id3
     * @return
     *    returns the array of trials created
     */
    public ArrayList<Trial> createTrials(){
        Profile profile1 = new Profile("id1");
        Profile profile2 = new Profile("id2");
        Profile profile3 = new Profile("id3");
        GeoLocation uni_loc = new GeoLocation(0.0, 0.0);
        ArrayList<Trial> trials = new ArrayList<>();

        for (int i=0; i<15; i++){
            if (i<4){
                trials.add(new TrialCount(""+i, uni_loc, profile1));
            } else if (i<10){
                trials.add(new TrialCount(""+i, uni_loc, profile2));
            } else {
                trials.add(new TrialCount(""+i, uni_loc, profile3));
            }
        }

        return trials;
    }

    /**
     * Tests blacklist methods by adding, bulk adding, deleting, bulk deleting
     * and then verifying that everything is as it should be afterwards
     */
    public void testBlacklist(){
        this.exp = new Experiment("foo");
        assertEquals("Wrong size blacklist", 0, this.exp.getBlacklist().size());
        ArrayList<String> mid_array = new ArrayList<>();
        ArrayList<String> temp_array;
        for (int i=0; i<30; i++){
            this.exp.addToBlacklist(""+i);
            if (i == 20){
                temp_array = this.exp.getBlacklist();
                for (int j=0; j<temp_array.size(); j++){
                    mid_array.add(temp_array.get(j));
                }
                assertEquals("getblacklist broken", 21, mid_array.size());
                assertEquals("Wrong size blacklist", 21, this.exp.getBlacklist().size());
                assertEquals("blacklist IN broken", true, this.exp.isBlacklisted("3"));
                this.exp.delFromBlacklist("3");
                this.exp.delFromBlacklist("4");
                assertEquals("del blacklist broken", 19, this.exp.getBlacklist().size());
                assertEquals("blacklist IN broken", false, this.exp.isBlacklisted("3"));
                this.exp.addManyBlacklist(mid_array);
                assertEquals("Add blacklist broken", 21, this.exp.getBlacklist().size());

            }
        }
        this.exp.addToBlacklist("31");
        assertEquals("add blacklist broken", 31, this.exp.getBlacklist().size());
        this.exp.delManyBlacklist(mid_array);
        assertEquals("del blacklists broken", 10, this.exp.getBlacklist().size());

    }

    /**
     * Tests the methods in experiment removeDupes, frequencies, median, Q1, Q3, std, and mean
     * By making some test data with known statistics
     */
    public void testStatistics(){
        Profile profile = new Profile("id1");
        GeoLocation uni_loc = new GeoLocation(0.0, 0.0);
        ArrayList<Trial> trials = new ArrayList<>();

        for (int i=1; i<101; i++){
            TrialIntCount trial = new TrialIntCount(""+i, uni_loc, profile, 0);
            if (i<40 && i%2==0){
                trial.setCount(i);

                trials.add(trial);
            } else if (i>= 40 && i<80 && i%3==0){
                trial.setCount(i);

                trials.add(trial);
            } else if ( i >= 80 && i%5==0) {
                trial.setCount(i);
                TrialIntCount trial2 = new TrialIntCount("1"+i, uni_loc, profile, 0);
                trial2.setCount(i);

                trials.add(trial);
                trials.add(trial2);
            }
        }

        this.exp = new Experiment("foo");
        this.exp.addTrials(trials);

        assertEquals("removeDupes does not work", 37, this.exp.removeDupes().length);
        assertEquals("frequencies does not work", 15, exp.frequencies().length);
        assertEquals("median does not work", "46.5", exp.getMedian());
        assertEquals("Q1 does not work", "23.00", exp.getQ1());
        assertEquals("Q3 does not work", "76.50", exp.getQ3());
        assertEquals("std does not work", "30.39", exp.getStd());
        assertEquals("mean does not work", "49.05", exp.getMean());

    }


}