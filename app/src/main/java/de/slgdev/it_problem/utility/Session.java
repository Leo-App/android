package de.slgdev.it_problem.utility;

import java.util.Hashtable;

import de.slgdev.it_problem.utility.datastructure.DecisionTree;

public class Session {

    private DecisionTree current;
    private String subject;

    public Session(String subject, Hashtable<String, DecisionTree> selection) {
        current = selection.get(subject);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return current.getContent().title;
    }

    public String getDescription() {
        return current.getContent().description;
    }

    public String getPath() {
        return current.getContent().pathToImage;
    }

    public boolean isAnswer() {
        return !current.hasChildren();
    }

    public boolean isAvailable() {
        return !current.isEmpty();
    }

    public void answerYes() {
        current = current.getRightTree();
    }

    public void answerNo() {
        current = current.getLeftTree();
    }

}
