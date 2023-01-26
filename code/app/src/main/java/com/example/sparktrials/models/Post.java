package com.example.sparktrials.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An abstract class that embodies questions and answers
 * Used in the forum
 */
public abstract class Post implements Comparable {
    private String id;
    private String body;
    private String expId;
    private Profile author;
//    private String author;
    private Date date;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /**
     * A constructor for post. A post always has a poster, some body, and for what experiment it was posted on
     * @param body
     * @param expId
     * @param author
     * @param date
     */
    Post(String id, String body, String expId, Profile author, Date date){
        this.id = id;
        this.body = body;
        this.expId = expId;
        this.author = author;
        this.date = date;

    }


    /**
     * Retrieves the experiment id of this post
     * @return
     *    The experiment id of the post
     */
    public String getExpId() {
        return expId;
    }

    /**
     * Retrieves the profile of the user who made the post
     * @return
     *    The profile of the use rwho made the post
     */
    public Profile getAuthor() {
        return author;
    }

    /**
     * Retrieves the body of the post
     * @return
     *    The body of the post
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieves the date of the post
     * @return
     */
    public Date getDate(){ return date; }

    /**
     * Get the date in a string format
     * @param date
     * @return A formatted string of the date.
     */
    public String getFormattedDate(Date date) {
        return formatter.format(date);
    }

    /**
     * Get the id of the post.
     * @return String representation of the id.
     */
    public String getId() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
        o = (Post) o;
        return date.compareTo(((Post) o).getDate());
    }
}
