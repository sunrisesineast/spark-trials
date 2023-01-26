package com.example.sparktrials.models;

import junit.framework.TestCase;

public class PostTest extends TestCase {
    /**
     * Test content getter, expId getter, profile getter, question getter, and answer getter
     * by verifying return results with inputted results
     */
    public void testGetters() {
        Profile profile1 = new Profile("proId1");
        Profile profile2 = new Profile("proId2");
        Question question = new Question("id1", "title","content", "expId", profile1);
        Answer answer = new Answer("id2", "content", "expId", profile2, question);
        question.addAnswer(answer);
        assertEquals("get content does not work", "content", question.getBody());
        assertEquals("get id does not work", "expId", question.getExpId());
        assertEquals("get profile does not work", "proId1", question.getAuthor().getId());
        assertEquals("get answers does not work", 1, question.getAnswers().size());
        assertEquals("get question does not work", "proId1", answer.getQuestion().getAuthor().getId());
        Answer answer2 = new Answer("id3", "content2", "expId", profile2, question);
        question.addAnswer(answer2);
        assertEquals("get answers does not work", 2, question.getAnswers().size());
    }
}