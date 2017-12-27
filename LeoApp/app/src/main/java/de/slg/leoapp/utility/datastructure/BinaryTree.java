package de.slg.leoapp.utility.datastructure;

/**
 * Materialien zu den zentralen NRW-Abiturpruefungen im Fach Informatik ab 2018
 *
 * Mithilfe der generischen Klasse de.slg.it.datastructure.BinaryTree koennen beliebig viele
 * Inhaltsobjekte vom Typ ContentType in einem Binaerbaum verwaltet werden. Ein
 * Objekt der Klasse stellt entweder einen leeren Baum dar oder verwaltet ein
 * Inhaltsobjekt sowie einen linken und einen rechten Teilbaum, die ebenfalls
 * Objekte der generischen Klasse BinaryTree sind.
 *
 * @author Qualitaets- und UnterstuetzungsAgentur - Landesinstitut fuer Schule
 * @version 2017.2712
 * @since 0.7.1
 */
@SuppressWarnings("WeakerAccess")
public class BinaryTree<ContentType> {

	/* --------- Anfang der privaten inneren Klasse -------------- */

    /**
     * Durch diese innere Klasse kann man dafuer sorgen, dass ein leerer Baum
     * null ist, ein nicht-leerer Baum jedoch immer eine nicht-null-Wurzel sowie
     * nicht-null-Teilbaeume, ggf. leere Teilbaeume hat.
     */
    private class BTNode<CT> {

        private CT content;
        private BinaryTree<CT> left, right;

        public BTNode(CT pContent) {
            // Der Knoten hat einen linken und einen rechten Teilbaum, die
            // beide von null verschieden sind. Also hat ein Blatt immer zwei
            // leere Teilbaeume unter sich.
            this.content = pContent;
            left = new BinaryTree<>();
            right = new BinaryTree<>();
        }

    }

	/* ----------- Ende der privaten inneren Klasse -------------- */

    private BTNode<ContentType> node;

    /**
     * Nach dem Aufruf des Konstruktors existiert ein leerer Binaerbaum.
     */
    public BinaryTree() {
        this.node = null;
    }

    /**
     * Wenn der Parameter pContent ungleich null ist, existiert nach dem Aufruf
     * des Konstruktors der Binaerbaum und hat pContent als Inhaltsobjekt und
     * zwei leere Teilbaeume. Falls der Parameter null ist, wird ein leerer
     * Binaerbaum erzeugt.
     *
     * @param pContent
     *            das Inhaltsobjekt des Wurzelknotens vom Typ ContentType
     */
    public BinaryTree(ContentType pContent) {
        if (pContent != null) {
            this.node = new BTNode<>(pContent);
        } else {
            this.node = null;
        }
    }

    /**
     * Wenn der Parameter pContent ungleich null ist, wird ein Binaerbaum mit
     * pContent als Inhalt und den beiden Teilbaeume pLeftTree und pRightTree
     * erzeugt. Sind pLeftTree oder pRightTree gleich null, wird der
     * entsprechende Teilbaum als leerer Binaerbaum eingefuegt. So kann es also
     * nie passieren, dass linke oder rechte Teilbaeume null sind. Wenn der
     * Parameter pContent gleich null ist, wird ein leerer Binaerbaum erzeugt.
     *
     * @param pContent
     *            das Inhaltsobjekt des Wurzelknotens vom Typ ContentType
     * @param pLeftTree
     *            der linke Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType>
     * @param pRightTree
     *            der rechte Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType>
     */
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
            // Da der Inhalt null ist, wird ein leerer BinarySearchTree erzeugt.
            this.node = null;
        }
    }

    /**
     * Diese Anfrage liefert den Wahrheitswert true, wenn der Binaerbaum leer
     * ist, sonst liefert sie den Wert false.
     *
     * @return true, wenn der Binaerbaum leer ist, sonst false
     */
    public boolean isEmpty() {
        return this.node == null;
    }

    /**
     * Wenn pContent null ist, geschieht nichts. <br />
     * Ansonsten: Wenn der Binaerbaum leer ist, wird der Parameter pContent als
     * Inhaltsobjekt sowie ein leerer linker und rechter Teilbaum eingefuegt.
     * Ist der Binaerbaum nicht leer, wird das Inhaltsobjekt durch pContent
     * ersetzt. Die Teilbaeume werden nicht geaendert.
     *
     * @param pContent
     *            neues Inhaltsobjekt vom Typ ContentType
     */
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

    /**
     * Diese Anfrage liefert das Inhaltsobjekt des Binaerbaums. Wenn der
     * Binaerbaum leer ist, wird null zurueckgegeben.
     *
     * @return das Inhaltsobjekt der Wurzel vom Typ ContentType bzw. null, wenn
     *         der Binaerbaum leer ist
     */
    public ContentType getContent() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.node.content;
        }
    }

    /**
     * Falls der Parameter null ist, geschieht nichts. Wenn der Binaerbaum leer
     * ist, wird pTree nicht angehaengt. Andernfalls erhaelt der Binaerbaum den
     * uebergebenen de.slg.it.datastructure.BinaryTree als linken Teilbaum.
     *
     * @param pTree
     *            neuer linker Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType>
     */
    public void setLeftTree(BinaryTree<ContentType> pTree) {
        if (!this.isEmpty() && pTree != null) {
            this.node.left = pTree;
        }
    }

    /**
     * Falls der Parameter null ist, geschieht nichts. Wenn der Binaerbaum leer
     * ist, wird pTree nicht angehaengt. Andernfalls erhaelt der Binaerbaum den
     * uebergebenen de.slg.it.datastructure.BinaryTree als rechten Teilbaum.
     *
     * @param pTree
     *            neuer linker Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType>
     */
    public void setRightTree(BinaryTree<ContentType> pTree) {
        if (!this.isEmpty() && pTree != null) {
            this.node.right = pTree;
        }
    }

    /**
     * Diese Anfrage liefert den linken Teilbaum des Binaerbaumes. Wenn der
     * Binaerbaum leer ist, wird null zurueckgegeben.
     *
     * @return linker Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType> oder null, wenn
     * der aktuelle Binaerbaum leer ist
     */
    public BinaryTree<ContentType> getLeftTree() {
        if (!this.isEmpty()) {
            return this.node.left;
        } else {
            return null;
        }
    }

    /**
     * Diese Anfrage liefert den rechten Teilbaum des Binaerbaumes. Wenn der
     * Binaerbaum (this) leer ist, wird null zurueckgegeben.
     *
     * @return rechter Teilbaum vom Typ de.slg.it.datastructure.BinaryTree<ContentType> oder null, wenn
     * der aktuelle Binaerbaum (this) leer ist
     */
    public BinaryTree<ContentType> getRightTree() {
        if (!this.isEmpty()) {
            return this.node.right;
        } else {
            return null;
        }
    }
}