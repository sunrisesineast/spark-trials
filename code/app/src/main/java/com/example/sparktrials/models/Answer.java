package com.example.sparktrials.models;

import java.util.Date;

public class Answer extends Post{
    private Question question;

    /**
     * An answer to a question for an experiment
     * @param body
     *   The body of the answer
     * @param expId
     *   The id of the experiment that this answer is part of
     * @param profile
     *   The profile of the user who wrote this answer
     * @param question
     *    The question that this answer is answering
     */
    public Answer(String id, String body, String expId, Profile profile, Question question){
        super(id, body, expId, profile, new Date());
        this.question = question;
    }

    /**
     * An answer to a question for an experiment
     * @param body
     *   The body of the answer
     * @param expId
     *   The id of the experiment that this answer is part of
     * @param profile
     *   The profile of the user who wrote this answer
     * @param question
     *    The question that this answer is answering
     */
    public Answer(String id, String body, String expId, Profile profile, Question question, Date date){
        super(id, body, expId, profile, date);
        this.question = question;
    }

    /**
     * Retrieves the question which this answer is answering
     * @return
     *   Returns a Question
     */
    public Question getQuestion() {
        return question;
    }

}
