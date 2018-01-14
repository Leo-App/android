package de.slg.it_problem.utility.datastructure;

import android.support.annotation.NonNull;

import de.slg.it_problem.utility.ProblemContent;
import de.slg.leoapp.utility.Utils;
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

        if (tree.contains(";l0;") && tree.contains(";R0;")) {
            setLeftTree(new DecisionTree(tree.substring(tree.indexOf(";L0;"), tree.indexOf(";R0;")), 0));
            setRightTree(new DecisionTree(tree.substring(tree.indexOf(";R0;")), 0));
        }
    }

    private DecisionTree(@NonNull String tree, int level) {
        if(tree.equals("") || tree.equals("_;;_"))
            return;
        //TODO
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

    @Override
    //preorder
    public String toString() {

        if(getContent() == null)
            return "";

        ProblemContent content = getContent();
        StringBuilder toString = new StringBuilder(content.title+"_;_"+content.description+"_;_"+content.pathToImage)
                .append("_;;_");

        if(getLeftTree() != null)
            toString.append(getLeftTree().toString());
        if(getRightTree() != null)
            toString.append(getRightTree().toString());

        return toString.toString();
    }

}
