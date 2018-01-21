package de.slgdev.umfragen.utility;

import java.util.HashMap;

public final class ResultListing {

    public final String  title;
    public final String  description;

    public HashMap<String, Integer> answers;

    public ResultListing(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ResultListing setAnswerMap(HashMap<String, Integer> answerMap) {
        answers = answerMap;
        return this;
    }

}
