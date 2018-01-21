package de.slgdev.leoapp.utility.datastructure;

import android.support.annotation.NonNull;

import java.util.Iterator;

/**
 * Um einige Methoden erweiterte doppelt verkettete Liste. Die Liste besitzt einen Pointer, der beliebig verschoben werden- und somit die einzelnen Inhaltsobjekte
 * adressieren kann.
 *
 * @param <ContentType> Inhaltsdatentyp
 * @author Moritz
 * @version 2017.2810
 * @since 0.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class List<ContentType> implements Iterable<ContentType> {

    private Node first, last, current;
    private int length;

    /**
     * Konstruktor.
     */
    public List() {
        first = null;
        last = null;
        current = null;
        length = 0;
    }

    /**
     * Konstruktor. Erlaubt das direkte Füllen der Liste mit dem Inhalt eines Arrays desselben Datentyps.
     *
     * @param array Neuer Listeninhalt
     */
    public List(ContentType[] array) {
        first = null;
        last = null;
        current = null;
        length = 0;
        adapt(array);
    }

    /**
     * Konstruktor. Erlaubt das direkte Füllen der Liste mit dem Inhalt einer {@link java.util.List} desselben Datentyps.
     *
     * @param list Neuer Listeninhalt
     */
    public List(java.util.List<ContentType> list) {
        first = null;
        last = null;
        current = null;
        length = 0;
        concat(list);
    }

    /**
     * Überprüft, ob die Liste Elemente enthält.
     *
     * @return true, wenn Liste leer, sonst false.
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Überprüft, ob der Listenpointer auf einem Element steht. Immer false, wenn die Liste leer ist.
     *
     * @return true wenn Pointer auf einem Listenelement.
     */
    public boolean hasAccess() {
        return current != null;
    }

    /**
     * Gibt true zurück, wenn das Listenelement, auf dem der Pointer steht, einen Vorgänger hat. Entspricht i.d.R. {@link #isFirst()}.
     *
     * @return Aktuelles Element hat einen Vorgänger
     */
    public boolean hasPrevious() {
        return hasAccess() && current.previous != null;
    }

    /**
     * Gibt true zurück, wenn das Listenelement, auf dem der Pointer steht, einen Nachfolger hat. Entspricht i.d.R. {@link #isLast()}.
     *
     * @return Aktuelles Element hat einen Vorgänger
     */
    public boolean hasNext() {
        return hasAccess() && current.next != null;
    }

    /**
     * Gibt true zurück, wenn das aktuelle Listenobjekt am Anfang der Liste steht.
     *
     * @return Aktuelles Element befindet sich am Anfang der Liste.
     */
    public boolean isFirst() {
        return current == first;
    }

    /**
     * Gibt true zurück, wenn das aktuelle Listenobjekt am Ende der Liste steht.
     *
     * @return Aktuelles Element befindet sich am Ende der Liste.
     */
    public boolean isLast() {
        return current == last;
    }

    /**
     * Verschiebt den Listenpointer um eine Stelle Richtung Ende der Liste.
     */
    public List<ContentType> next() {
        current = current.next;
        return this;
    }

    /**
     * Verschiebt den Listenpointer um eine Stelle Richtung Anfang der Liste.
     */
    public List<ContentType> previous() {
        current = current.previous;
        return this;
    }

    /**
     * Verschiebt den Listenpointer zum Anfang der Liste.
     */
    public List<ContentType> toFirst() {
        if (!isEmpty()) {
            current = first;
        }
        return this;
    }

    /**
     * Verschiebt den Listenpointer zum Ende der Liste.
     */
    public List<ContentType> toLast() {
        if (!isEmpty()) {
            current = last;
        }
        return this;
    }

    /**
     * Verschiebt den Listenpointer zu einem definierten Index.
     *
     * @param index Zielindex
     */
    public List<ContentType> toIndex(int index) {
        if (index >= length - 1)
            toLast();
        else
            for (toFirst(); hasAccess() && index > 0; next(), index--)
                ;
        return this;
    }

    /**
     * Tauscht zwei Listenelemente. Der Listenpointer steht danach auf firstIndex.
     *
     * @param firstIndex  Index des ersten zu vertauschenden Elements
     * @param secondIndex Index des zweiten zu vertauschenden Elements
     */
    public void swap(int firstIndex, int secondIndex) {
        toIndex(firstIndex);
        ContentType t1 = getContent();

        toIndex(secondIndex);
        ContentType t2 = getContent();

        remove();
        insertBefore(t1);

        toIndex(firstIndex);
        remove();
        insertBefore(t2);
    }

    /**
     * Gibt das Objekt an der aktuellen Position des Pointers zurück
     *
     * @return Aktuelles Objekt.
     */
    public ContentType getContent() {
        if (this.hasAccess())
            return current.content;
        return null;
    }

    /**
     * Ersetzt das Objekt an der aktuellen Listenposition durch ein per Parameter Übergebenes.
     *
     * @param pContent Neues Listenobjekt
     */
    public void setContent(ContentType pContent) {
        if (pContent != null && this.hasAccess())
            current.content = pContent;
    }

    /**
     * Liefert das Objekt nach dem Aktuellen, ohne den Pointer zu verschieben.
     *
     * @return Folgendes Listenobjekt.
     */
    public ContentType getNext() {
        if (hasAccess() && current.next != null)
            return current.next.content;
        return null;
    }

    /**
     * Liefert das Objekt vor dem Aktuellen, ohne den Pointer zu verschieben.
     *
     * @return Vorheriges Listenobjekt.
     */
    public ContentType getPrevious() {
        if (hasAccess() && current.previous != null)
            return current.previous.content;
        return null;
    }

    /**
     * Fügt ein neues Objekt vor die Position des Pointers ein, steht dieser auf dem null-Objekt ({@link #hasAccess()} returns false), wird das Objekt ans
     * Ende der Liste angehängt. Der Pointer wird nicht verschoben.
     *
     * @param pContent Neues Listenobjekt
     */
    public void insertBefore(ContentType pContent) {
        if (pContent != null) {
            if (hasAccess()) {
                Node newNode = new Node(pContent);
                if (current == first)
                    first = newNode;
                newNode.insertBefore(current, newNode == first);
                length++;
            } else {
                if (isEmpty()) {
                    Node newNode = new Node(pContent);
                    first = newNode;
                    last = newNode;
                    length = 1;
                } else {
                    append(pContent);
                }
            }
        }
    }

    /**
     * Fügt ein neues Objekt hinter die Position des Pointers ein, steht dieser auf dem null-Objekt ({@link #hasAccess()} returns false), wird das Objekt ans
     * Ende der Liste angehängt. Der Pointer wird nicht verschoben.
     *
     * @param pContent Neues Listenobjekt
     */
    public void insertBehind(ContentType pContent) {
        if (pContent != null) {
            if (hasAccess()) {
                Node newNode = new Node(pContent);
                newNode.insertBehind(current, newNode == last);
                length++;
            } else {
                if (isEmpty()) {
                    Node newNode = new Node(pContent);
                    first = newNode;
                    last = newNode;
                    length = 1;
                } else {
                    append(pContent);
                }
            }
        }
    }

    /**
     * Hängt ein neues Objekt ans Ende der Liste an ohne den Pointer zu verschieben.
     *
     * @param pContent Neues Listenobjekt
     * @return Instanz der geänderten Liste
     */
    public List<ContentType> append(ContentType pContent) {
        if (pContent != null) {
            if (this.isEmpty()) {
                this.insertBefore(pContent);
            } else {
                Node newNode = new Node(pContent);
                newNode.insertBehind(last, true);
                last = newNode;
                length++;
            }
        }
        return this;
    }

    /**
     * Hängt eine native java.util Liste ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param pList {@link java.util.List}, die angehängt werden soll.
     * @return Instanz der geänderten Liste.
     */
    public List<ContentType> concat(java.util.List<ContentType> pList) {
        for (ContentType t : pList) {
            append(t);
        }
        return this;
    }

    /**
     * Hängt eine andere Liste ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param pList Liste, die angehängt werden soll.
     * @return Instanz der geänderten Liste.
     */
    public List<ContentType> concat(List<ContentType> pList) {
        if (pList != this && pList != null && !pList.isEmpty()) {
            if (this.isEmpty()) {
                first = pList.first;
                last = pList.last;
            } else {
                last.next = pList.first;
                pList.first.previous = last;
                last = pList.last;
            }
            pList.first = null;
            pList.last = null;
            pList.current = null;
            length += pList.size();
        }
        return this;
    }

    /**
     * Entfernt das Objekt, auf das der Pointer zeigt. Der Pointer steht danach auf dem Nachfolgeelement.
     */
    public void remove() {
        if (this.hasAccess() && !this.isEmpty()) {
            if (current == first)
                first = first.next;
            else
                current.previous.next = current.next;
            if (current == last)
                last = current.previous;
            else
                current.next.previous = current.previous;
            Node temp = current;
            next();
            temp.content = null;
            temp.next = null;
            temp.previous = null;
            length--;
        }
    }

    /**
     * Hängt ein Array ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param array Array, das angehängt werden soll.
     */
    public void adapt(ContentType[] array) {
        for (ContentType c : array)
            append(c);
    }

    /**
     * Füllt ein Array desselben Datentyps mit dem Inhalt der Liste
     *
     * @param array Zu befüllendes Array
     * @return ""Gefülltes" Array
     */
    public ContentType[] fill(ContentType[] array) {
        toFirst();
        for (int i = 0; i < array.length; i++, next())
            array[i] = getContent();
        return array;
    }

    /**
     * Liefert die Größe der Liste.
     *
     * @return Listengröße
     */
    public int size() {
        return length;
    }

    /**
     * Überprüft, ob die Liste ein bestimmtes Objekt enthält
     *
     * @param object Zu überprüfendes Objekt.
     * @return true, wenn Objekt Element der Liste.
     */
    public boolean contains(ContentType object) {
        for (toFirst(); hasAccess(); next()) {
            if (object.equals(getContent()))
                return true;
        }
        return false;
    }

    /**
     * Liefert das Objekt an einem bestimmten Listenindex. Entspricht Aufrufen von {@link #toIndex(int)} und anschließend {@link #getContent()}.
     *
     * @param index Listenindex
     * @return Listenobjekt
     */
    public ContentType getObjectAt(int index) {
        toIndex(index);
        return getContent();
    }

    /**
     * Gibt einen Iterator der Liste zurück.
     *
     * @return Iterator
     */
    @Override
    @NonNull
    public Iterator<ContentType> iterator() {
        current = null;
        return new Iterator<ContentType>() {
            @Override
            public boolean hasNext() {
                return current != last;
            }

            @Override
            public ContentType next() {
                if (hasAccess())
                    List.this.next();
                else
                    toFirst();
                return getContent();
            }
        };
    }

    private class Node {
        ContentType content;
        Node        next, previous;

        Node(ContentType contentObject) {
            this.content = contentObject;
            next = null;
            previous = null;
        }

        void insertBehind(Node pNode, boolean newLast) {
            this.next = pNode.next;
            this.previous = pNode;
            pNode.next = this;
            if (!newLast)
                this.next.previous = this;
        }

        void insertBefore(Node pNode, boolean newFirst) {
            this.previous = pNode.previous;
            this.next = pNode;
            pNode.previous = this;
            if (!newFirst)
                this.previous.next = this;
        }
    }
}