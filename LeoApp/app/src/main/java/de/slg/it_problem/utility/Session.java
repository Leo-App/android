package de.slg.it_problem.utility;

import java.util.Hashtable;

import de.slg.it_problem.utility.datastructure.DecisionTree;

public class Session {

    private DecisionTree current;
    private String subject;

    Session(String subject, Hashtable<String, DecisionTree> selection) {
        current = selection.get(subject);
        this.subject = subject;
    }

    String getSubject() {
        return subject;
    }

    String getTitle() {
        return current.getContent().title;
    }

    String getDescription() {
        return current.getContent().description;
    }

    String getPath() {
        return current.getContent().pathToImage;
    }

    boolean isAnswer() {
        return current.getLeftTree().isEmpty() && current.getRightTree().isEmpty();
    }

    void answerYes() {
        current = current.getRightTree();
    }

    void answerNo() {
        current = current.getLeftTree();
    }

}
