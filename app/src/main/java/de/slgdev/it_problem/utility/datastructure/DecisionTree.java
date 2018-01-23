package de.slgdev.it_problem.utility.datastructure;

import android.support.annotation.NonNull;

import de.slgdev.it_problem.utility.ProblemContent;
import de.slgdev.leoapp.utility.datastructure.BinaryTree;

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

        if (tree.contains(";L1;") && tree.contains(";R1;")) {
            setLeftTree(new DecisionTree(tree.substring(tree.indexOf(";L1;"), tree.indexOf(";R1;")), 1));
            setRightTree(new DecisionTree(tree.substring(tree.indexOf(";R1;")), 1));
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

    public boolean hasChildren() {
        return !(super.getLeftTree().isEmpty() && super.getRightTree().isEmpty());
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

    @Override
    //preorder
    public String toString() {
        return toString(0);
    }

    private String toString(int level) {
        if(getContent() == null)
            return "";

        StringBuilder toString = new StringBuilder(getContent().toString())
                .append("_;;_");

        level++;

        if(!super.getLeftTree().isEmpty())
            toString.append(";L").append(level).append(";").append(getLeftTree().toString(level));
        if(!super.getRightTree().isEmpty()) {
            toString.append(";R").append(level).append(";").append(getRightTree().toString(level));
        }

        return toString.toString();
    }

}
