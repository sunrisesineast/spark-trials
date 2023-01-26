package com.example.sparktrials.models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Question extends Post {
    private String title;
    private ArrayList<Answer> answers;

    /**
     * A constructor for initializing a question
     * @param body
     *    The content (text body) of the post
     * @param expId
     *    The id of the experiment its for
     * @param author
     *    The author of the poster
     */
    public Question(String id, String title, String body, String expId, Profile author){
        super(id, body, expId, author, new Date());
        this.title = title;
        this.answers = new ArrayList<>();
    }


    /**
     * A constructor for rebuilding a question from the database
     * @param body
     *    The content (text body) of the post
     * @param expId
     *    The id of the experiment its for
     * @param author
     *    The profile of the poster
     * @param date
     *    The date and time when the post was created
     */
    public Question(String id, String title, String body, String expId, Profile author, Date date){
        super(id, body, expId, author, date);
        this.title = title;
        this.answers = new ArrayList<>();
    }


    /**
     * A constructor for rebuilding a question from the database
     * @param body
     *    The content (text body) of the post
     * @param expId
     *    The id of the experiment its for
     * @param author
     *    The profile of the poster
     * @param answers
     *    The list of answers that this post has
     * @param date
     *    The date and time when the post was created
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Question(String id, String title, String body, String expId, Profile author, ArrayList<Answer> answers, Date date){
        super(id, body, expId, author, date);
        this.title = title;
        this.answers = answers;
    }

    /**
     * Gets the array of answers that this post has
     * @return
     *    An array of answers
     */
    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    /**
     * Replaces the list of answers with the one passed
     * Should not be used unless to setup a question
     * @param answers
     *    A list of new answers
     */
    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    /**
     * Adds a given answer to the list of answers
     * @param answer
     *    An answer, ideally an answer to this question
     */
    public void addAnswer(Answer answer){
        this.answers.add(answer);
    }

    /**
     * Get the title of the question.
     * @return string representation of the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the count of answers in this question.
     * @return int representation of count.
     */
    public int getAnswerCount() {
        return answers.size();
    }


    /**
     * Sort the answers arraylist in a latest first order.
     * @param latestFirst A boolean value to determine if sort the list in latest first.
     *                    If false, sort the list in earliest first order.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortAnswersLatestFirst(Boolean latestFirst) {
        answers.sort((d1,d2) -> d1.compareTo(d2));
        if (latestFirst)
            reverseAnswers();
    }

    /**
     * Reverse the answers arraylist.
     */
    public void reverseAnswers() {
        Collections.reverse(this.answers);
    }

    /**
     * Get the latest reply time in answers.
     * @return The date of the latest reply in answers.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Date getLastReplyTime() {
        sortAnswersLatestFirst(true);
        if (answers.isEmpty())
            return getDate();
        return answers.get(0).getDate();
    }

    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int compareTo(Object o) {
        o = (Question) o;
        return getLastReplyTime().compareTo(((Question) o).getLastReplyTime());
    }
}
