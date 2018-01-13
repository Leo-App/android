package de.slg.it_problem.utility;

/**
 * ProblemContent.
 *
 * Inhaltsobjekt für den {@link de.slg.it_problem.utility.datastructure.DecisionTree Entscheidungsbaum}.
 * Verwaltet die Fragen und Antworten des ITProblemlösers.
 *
 * @author Gianni
 * @since 0.7.2
 * @version 2017.2912
 */
public class ProblemContent {

    public final String title;
    public final String description;
    public final String pathToImage;

    public ProblemContent(String title, String description, String pathToImage) {
        this.title = title;
        this.description = description;
        this.pathToImage = pathToImage.equals("null") ? null : pathToImage;
    }

}
