package com.example.sparktrials.models;

import java.util.ArrayList;

/**
 * This class keeps track of each user profile with an id, their username and contact info
 */
public class Profile {
    private String id;
    private String username;
    private String contact;
    private ArrayList<String> subscriptions;
    /**
     * This constructor creates a default Profile with no attributes
     */
    public Profile(){
        this.id = null;
        this.username = null;
        this.contact = null;
        this.subscriptions = new ArrayList<>();
    }

    /**
     * This constructor initializes a unique profile
     * @param id
     *    A unique id used to identify this profile. Generated elsewhere and passed up
     *    Default username will be based on their id
     */
    public Profile(String id){
        this.id = id;
        this.username = "user"+id;
        this.contact = null;
        this.subscriptions = new ArrayList<>();

    }

    /**
     * This constructor fills out a profile
     * @param id
     *    A unique id used to identify this profile
     * @param username
     *    The username of the person
     * @param contact
     *    The contact info of the person
     */
    public Profile(String id, String username, String contact){
        this.id = id;
        this.username = username;
        this.contact = contact;
    }

    /**
     * This constructor fills out a profile
     * TO BE USED WHEN DOWNLOADING A PRE-EXISTING profile
     * @param id
     *    A unique id used to identify this profile
     * @param username
     *    The username of the person
     * @param contact
     *    The contact info of the person
     * @param subscriptions
     *    A list of experiments that the profile is subscribed to
     */
    public Profile(String id, String username, String contact, ArrayList<String> subscriptions){
        this.id = id;
        this.username = username;
        this.contact = contact;
        this.subscriptions = subscriptions;
    }

    /**
     * This method returns the profile's id
     * @return
     *    Returns the id of this profile
     */
    public String getId() {
        return id;
    }

    /**
     * This method initializes the id of the user
     * id should not overwrite an existing id
     * @param id
     *    New id that this profile will take
     */
    public void setId(String id) {
        if (this.id == null){
            this.id = id;
        }
    }

    /**
     * This method overwrites the id of the user
     * Irreversible
     * @param id
     *    New id that this profile will take
     */
    public void overwriteId(String id) {
        this.id = id;
    }

    /**
     * This method retrieves the name of this profile
     * @return
     *    Returns the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method sets the username of the profile
     * @param username
     *    The new username that this profile will take
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This method retrieves the contact info from this profile
     * @return
     *    Returns the contact info from the profile
     */
    public String getContact() {
        return contact;
    }

    /**
     * Overriding equals() method from object class for Profiles
     * @param o
     *  The object to compare with
     * @return
     *  True if the Profiles have the same ID
     *  False otherwise
     */
    @Override
    public boolean equals(Object o){
        boolean retVal = false;

        if(o instanceof Profile){
            Profile profile = (Profile) o;
            retVal = profile.getId().equals(this.id);
        }

        return retVal;
    }

    /**
     * This method sets the contact info of the profile
     * @param contact
     *    The new contact that this profile will take
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * This method gets the subscriptions that a profile has
     * @return
     *    A list containing the ids of every experiment that the user is subscribed to
     */
    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    /**
     * Replaces subscription list
     * Should not really be used except to create a Profile
     * @param subscriptions
     *    A list of experiment ids
     */
    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    /**
     * Adds an experiment id to the list of subscriptions
     * Only add it if it doesn't already exist inside it
     * @param expId
     *    And experiment's id
     */
    public void addSubscription(String expId) {
        if (!this.isSubscribed(expId)) {
            this.subscriptions.add(expId);
        }
    }

    /**
     * Delete an experiment id in the list of subscriptions
     * @param expId
     *    The experiment id to delete
     */
    public void delSubscription(String expId) {
        this.subscriptions.remove(expId);
    }

    /**
     * Adds a list of experiment ids to the list of subscriptions
     * @param expIds
     *    A list of experiment ids
     */
    public void addSubscriptions(ArrayList<String> expIds){
        for (int i=0; i<expIds.size(); i++){
            this.addSubscription(expIds.get(i));
        }
    }

    /**
     * Delete a group of experiment ids from the last of subscriptions
     * @param expIds
     *   A list of experiment ids
     */
    public void delSubscriptions(ArrayList<String> expIds){
        this.subscriptions.removeAll(expIds);
    }

    /**
     * Checks if an experiment's id is in this profile's subscribed experiments
     * @param expId
     *    The experiment id to check for
     * @return
     *    returns true if experiment id is in subscriptions
     */
    public boolean isSubscribed(String expId) {
        return this.subscriptions.contains(expId);
    }
}