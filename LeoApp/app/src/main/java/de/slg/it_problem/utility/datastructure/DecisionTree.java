package de.slg.it_problem.utility.datastructure;

import android.support.annotation.NonNull;

import de.slg.it_problem.utility.ProblemContent;
import de.slg.leoapp.utility.datastructure.BinaryTree;

/**
 * DecisionTree.
 *
 * Subklasse von BinaryTree, um einige Methoden für den Entscheidungsbaum erweitert.
 *
 * @author Gianni
 * @since 0.7.1
 * @version 2017.2912
 */
public class DecisionTree extends BinaryTree<ProblemContent> {

    /**
     * Konstruktor.
     *
     * Erstellt den Baum anhand eines Strings. Format des Strings: ;(L/R)(Level);Inhalt_;;_;(L/R)(Level);Inhalt_;;_ ...
     *
     * @param tree Stringrepräsentation des Baums
     */
    public DecisionTree(@NonNull String tree) {

        if (tree.equals(""))
            return;

        String current = tree.split("_;;_")[0];
        String[] data = current.split("_;_");

        setContent(new ProblemContent(data[0], data[1], data[2]));

        if (tree.contains(";L0;") && tree.contains(";R0;")) {
            setLeftTree(new DecisionTree(tree.substring(tree.indexOf(";L0;"), tree.indexOf(";R0;")), 0));
            setRightTree(new DecisionTree(tree.substring(tree.indexOf(";R0;")), 0));
        }
    }

    private DecisionTree(@NonNull String tree, int level) {
        if(tree.equals("") || tree.equals("_;;_"))
            return;

        String current = tree.split("_;;_")[0].substring(tree.indexOf(level+";")+2);
        String[] data = current.split("_;_");

        setContent(new ProblemContent(data[0], data[1], data[2]));
        level++;

        if (tree.contains(";L"+level+";") && tree.contains(";R"+level+";")) {
            setLeftTree(new DecisionTree(tree.substring(tree.indexOf(";L"+level+";"), tree.indexOf(";R"+level+";")), level));
            setRightTree(new DecisionTree(tree.substring(tree.indexOf(";R"+level+";")), level));
        }

    }

    /**
     * Standardkonstruktor.
     */
    public DecisionTree() {
        super();
    }

    public DecisionTree getLeftTree() {
        return (DecisionTree) super.getLeftTree();
    }

    public DecisionTree getRightTree() {
        return (DecisionTree) super.getRightTree();
    }

    private void setLeftTree(DecisionTree tree) {
        super.setLeftTree(tree);
    }

    private void setRightTree(DecisionTree tree) {
        super.setRightTree(tree);
    }

}
