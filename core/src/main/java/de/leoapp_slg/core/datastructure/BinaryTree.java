package de.leoapp_slg.core.datastructure;

import android.support.annotation.NonNull;

import java.util.Iterator;

import de.leoapp_slg.core.datastructure.exception.NodeIndexOutOfBoundsException;

/**
 * Nicht lineare Datenstruktur, bei der Elemente in einer Baumstruktur angeordnet sind. Dabei hat
 * ist jedes Element "Wurzel" für zwei weitere, untergeordnete Elemente
 *
 * @author Gianni
 * @version 2017.2912
 * @since 0.7.2
 */
@SuppressWarnings("all")
public class BinaryTree<ContentType> implements Iterable<ContentType> {

    private BTNode<ContentType> node;

    public BinaryTree() {
        this.node = null;
    }

    public BinaryTree(ContentType pContent) {
        if (pContent != null) {
            this.node = new BTNode<>(pContent);
        } else {
            this.node = null;
        }
    }

    public BinaryTree(ContentType pContent, BinaryTree<ContentType> pLeftTree, BinaryTree<ContentType> pRightTree) {
        if (pContent != null) {
            this.node = new BTNode<>(pContent);
            if (pLeftTree != null) {
                this.node.left = pLeftTree;
            } else {
                this.node.left = new BinaryTree<>();
            }
            if (pRightTree != null) {
                this.node.right = pRightTree;
            } else {
                this.node.right = new BinaryTree<>();
            }
        } else {
            this.node = null;
        }
    }

    public boolean isEmpty() {
        return this.node == null;
    }

    public void setContent(ContentType pContent) {
        if (pContent != null) {
            if (this.isEmpty()) {
                node = new BTNode<>(pContent);
                this.node.left = new BinaryTree<>();
                this.node.right = new BinaryTree<>();
            }
            this.node.content = pContent;
        }
    }

    public ContentType getContent() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.node.content;
        }
    }

    public void setLeftTree(BinaryTree<ContentType> pTree) {
        if (!this.isEmpty() && pTree != null) {
            this.node.left = pTree;
        }
    }

    public void setRightTree(BinaryTree<ContentType> pTree) {
        if (!this.isEmpty() && pTree != null) {
            this.node.right = pTree;
        }
    }

    public BinaryTree<ContentType> getLeftTree() {
        if (!this.isEmpty()) {
            return this.node.left;
        } else {
            return null;
        }
    }

    public BinaryTree<ContentType> getRightTree() {
        if (!this.isEmpty()) {
            return this.node.right;
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    //preorder
    public Iterator<ContentType> iterator() {
        return new Iterator<ContentType>() {
            @Override
            public boolean hasNext() {
                BinaryTree<ContentType> current = BinaryTree.this;
                while (true) {
                    if (!current.getRightTree().isEmpty())
                        current = current.getRightTree();
                    else if (!current.getLeftTree().isEmpty())
                        current = current.getLeftTree();
                    else
                        return current.node.marked;
                }
            }

            @Override
            public ContentType next() throws NodeIndexOutOfBoundsException {

                BinaryTree.this.node.marked = true;

                if (!BinaryTree.this.node.marked)
                    return BinaryTree.this.node.content;
                else if (getLeftTree().iterator().hasNext())
                    return getLeftTree().iterator().next();
                else if (getRightTree().iterator().hasNext())
                    return getRightTree().iterator().next();
                else
                    throw new NodeIndexOutOfBoundsException("No new nodes available");

            }

        };
    }

    @Override
    //preorder
    public String toString() {
        return toString(0);
    }

    private String toString(int level) {
        if (getContent() == null)
            return "";

        StringBuilder toString = new StringBuilder(getContent().toString())
                .append("_;;_");

        level++;

        if (!getLeftTree().isEmpty())
            toString.append(";L").append(level).append(";").append(getLeftTree().toString(level));
        if (!getRightTree().isEmpty()) {
            toString.append(";R").append(level).append(";").append(getRightTree().toString(level));
        }

        return toString.toString();
    }

    private class BTNode<CT> {

        private CT content;
        private BinaryTree<CT> left, right;
        private boolean marked;

        public BTNode(CT pContent) {
            this.content = pContent;
            left = new BinaryTree<>();
            right = new BinaryTree<>();
        }

    }

}