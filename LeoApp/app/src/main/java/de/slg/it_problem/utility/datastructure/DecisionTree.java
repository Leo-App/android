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
     * Erstellt den Baum anhand eines Strings. Format des Strings: Inhalt_;;_
     *
     * @param tree Stringrepräsentation des Baums
     */
    public DecisionTree(@NonNull String tree) {
        super();

        if(tree.equals("") || tree.equals("_;;_"))
            return;

        String current = tree.substring(0, tree.indexOf("_;;_"));
        String[] params = current.split("_;_");

        setContent(new ProblemContent(params[0], params[1], params[2]));

        tree = tree.substring(tree.indexOf("_;;_")+3);

        String[] components = tree.split("_;;_");
        int centerIndex = components.length/2;

        StringBuilder left = new StringBuilder();
        for (int i = 0; i < centerIndex; i++) {
            left.append(components[i]).append("_;;_");
        }

        StringBuilder right = new StringBuilder();
        for (int i = centerIndex; i < components.length; i++) {
            right.append(components[i]).append("_;;_");
        }

        DecisionTree leftTree = new DecisionTree(left.toString());
        DecisionTree rightTree = new DecisionTree(right.toString());

        setLeftTree(leftTree);
        setRightTree(rightTree);

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

        StringBuilder toString = new StringBuilder(content.title+"_;_"+content.description+"_;_"+content.pathToImage+"_;_")
                .append("_;;_");

        if(getLeftTree() != null)
            toString.append(getLeftTree().toString());
        if(getRightTree() != null)
            toString.append(getRightTree().toString());

        return toString.toString();
    }

}
