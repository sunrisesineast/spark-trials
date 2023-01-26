package com.example.sparktrials.models;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Class to test the Profile class and all its methods
 */
public class ProfileTest extends TestCase {

    Profile profile;

    /**
     * Tests getId by verifying that getId retrieves the prescribed value
     */
    public void testGetId() {
        this.profile = new Profile("23#(&4|`@anything!", "foo", "foo");
        assertEquals("getId not working", "23#(&4|`@anything!", profile.getId());
    }

    /**
     * Tests setId by verifying that the new id is the value that Profile now holds,
     * but that the update only goes through if the id was null initially
     */
    public void testSetId() {
        this.profile = new Profile(null, "foo", "foo");
        this.profile.setId("=anything!23#(&4|`@)");
        assertEquals("setId not working", "=anything!23#(&4|`@)", profile.getId());
        this.profile.setId("foo");
        assertEquals("setId not preventing overwrites", "=anything!23#(&4|`@)", profile.getId());
    }

    /**
     * Tests overwriteId by verifying that the new id is the id that Profile now holds
     */
    public void testOverwriteId() {
        this.profile = new Profile("foo", "foo", "foo");
        this.profile.overwriteId("anything!23#(&4|`@)");
        assertEquals("overwriteId not working", "anything!23#(&4|`@)", profile.getId());
    }

    /**
     * Tests getUsername by verifying that the returned username is the usernameheld by profile
     */
    public void testGetUsername() {
        this.profile = new Profile("foo", "=anything!23#(&4|`@)", "foo");
        assertEquals("getUsername not working", "=anything!23#(&4|`@)", profile.getUsername());
    }

    /**
     * Tests setUsername by verifying that the new username is the username that profile now holds
     */
    public void testSetUsername() {
        this.profile = new Profile("foo", "foo", "foo");
        profile.setUsername("=anything!23#(&4|`@)");
        assertEquals("setUsername not working", "=anything!23#(&4|`@)", profile.getUsername());
    }

    /**
     * Tests getContact by verifying that that the returned contact is the contact held by profile
     */
    public void testGetContact() {
        this.profile = new Profile("foo", "foo", "=anything!23#(&4|`@)");
        assertEquals("getContact not working", "=anything!23#(&4|`@)", profile.getContact());

    }

    /**
     * Tests setContact by verifying that the new contact is the contact that profile now holds
     */
    public void testSetContact() {
        this.profile = new Profile("foo", "foo", "foo");
        profile.setContact("=anything!23#(&4|`@)");
        assertEquals("setContact not working", "=anything!23#(&4|`@)", profile.getContact());
    }

    /**
     * Tests subscription methods by adding, bulk adding, deleting, bulk deleting
     * and then verifying that everything is as it should be afterwards
     */
    public void testSubscriptions(){
        this.profile = new Profile("foo");
        assertEquals("Wrong size subscriptions", 0, this.profile.getSubscriptions().size()); //
        ArrayList<String> mid_array = new ArrayList<>();
        ArrayList<String> temp_array;
        for (int i=0; i<30; i++){
            this.profile.addSubscription(""+i);
            if (i == 20){
                temp_array = this.profile.getSubscriptions();
                for (int j=0; j<temp_array.size(); j++){
                    mid_array.add(temp_array.get(j));
                }
                assertEquals("getSubscriptions broken", 21, mid_array.size());
                assertEquals("Wrong size subscriptions", 21, this.profile.getSubscriptions().size());
                assertEquals("Subscription IN broken", true, this.profile.isSubscribed("3"));
                this.profile.delSubscription("3");
                this.profile.delSubscription("4");
                assertEquals("del subscription broken", 19, this.profile.getSubscriptions().size());
                assertEquals("Subscription IN broken", false, this.profile.isSubscribed("3"));
                this.profile.addSubscriptions(mid_array);
                assertEquals("Add Subscription broken", 21, this.profile.getSubscriptions().size());

            }
        }
        this.profile.addSubscription("31");
        assertEquals("add subscription broken", 31, this.profile.getSubscriptions().size());
        this.profile.delSubscriptions(mid_array);
        assertEquals("del subscriptions broken", 10, this.profile.getSubscriptions().size());

    }
}