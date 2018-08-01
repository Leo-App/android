@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.leoapp_slg.core.datastructure

import androidx.annotation.NonNull

/**
 * Um einige Methoden erweiterte doppelt verkettete Liste. Die Liste besitzt einen Pointer, der beliebig verschoben werden- und somit die einzelnen Inhaltsobjekte
 * adressieren kann.
 *
 * @param <ContentType> Inhaltsdatentyp
 * @author Moritz
 * @version 2017.2810
 * @since 0.0.1
 */
class List<ContentType>() : Iterable<ContentType> {
    private var first: Node?
    private var last: Node?
    private var current: Node?
    private var length: Int = 0

    /**
     * Konstruktor.
     */
    init {
        first = null
        last = null
        current = null
        length = 0
    }

    /**
     * Konstruktor. Erlaubt das direkte Füllen der Liste mit dem Inhalt eines Arrays desselben Datentyps.
     *
     * @param array Neuer Listeninhalt
     */
    constructor(array: Array<ContentType>) : this() {
        concat(array)
    }

    /**
     * Konstruktor. Erlaubt das direkte Füllen der Liste mit dem Inhalt einer [java.util.List] desselben Datentyps.
     *
     * @param list Neuer Listeninhalt
     */
    constructor(list: List<ContentType>) : this() {
        concat(list)
    }

    /**
     * Überprüft, ob die Liste Elemente enthält.
     *
     * @return true, wenn Liste leer, sonst false.
     */
    fun isEmpty(): Boolean {
        return first == null
    }

    /**
     * Überprüft, ob der Listenpointer auf einem Element steht. Immer false, wenn die Liste leer ist.
     *
     * @return true wenn Pointer auf einem Listenelement.
     */
    fun hasAccess(): Boolean {
        return current != null
    }

    /**
     * Gibt true zurück, wenn das Listenelement, auf dem der Pointer steht, einen Vorgänger hat. Entspricht i.d.R. [.isFirst].
     *
     * @return Aktuelles Element hat einen Vorgänger
     */
    fun hasPrevious(): Boolean {
        return hasAccess() && current!!.previous != null
    }

    /**
     * Gibt true zurück, wenn das Listenelement, auf dem der Pointer steht, einen Nachfolger hat. Entspricht i.d.R. [.isLast].
     *
     * @return Aktuelles Element hat einen Vorgänger
     */
    fun hasNext(): Boolean {
        return hasAccess() && current!!.next != null
    }

    /**
     * Gibt true zurück, wenn das aktuelle Listenobjekt am Anfang der Liste steht.
     *
     * @return Aktuelles Element befindet sich am Anfang der Liste.
     */
    fun isFirst(): Boolean {
        return current === first
    }

    /**
     * Gibt true zurück, wenn das aktuelle Listenobjekt am Ende der Liste steht.
     *
     * @return Aktuelles Element befindet sich am Ende der Liste.
     */
    fun isLast(): Boolean {
        return current === last
    }

    /**
     * Verschiebt den Listenpointer um eine Stelle Richtung Ende der Liste.
     */
    fun next(): List<ContentType> {
        current = current!!.next
        return this
    }

    operator fun inc(): List<ContentType> {
        return next()
    }

    /**
     * Verschiebt den Listenpointer um eine Stelle Richtung Anfang der Liste.
     */
    fun previous(): List<ContentType> {
        current = current!!.previous
        return this
    }

    operator fun dec(): List<ContentType> {
        return previous()
    }

    /**
     * Verschiebt den Listenpointer zum Anfang der Liste.
     */
    fun toFirst(): List<ContentType> {
        if (!isEmpty()) {
            current = first
        }
        return this
    }

    /**
     * Verschiebt den Listenpointer zum Ende der Liste.
     */
    fun toLast(): List<ContentType> {
        if (!isEmpty()) {
            current = last
        }
        return this
    }

    /**
     * Verschiebt den Listenpointer zu einem definierten Index.
     *
     * @param index Zielindex
     */
    fun toIndex(index: Int): List<ContentType> {
        var i = index
        if (i >= length - 1)
            toLast()
        else {
            toFirst()
            while (hasAccess() && i > 0) {
                next()
                i--
            }
        }
        return this
    }

    /**
     * Tauscht zwei Listenelemente. Der Listenpointer steht danach auf firstIndex.
     *
     * @param firstIndex  Index des ersten zu vertauschenden Elements
     * @param secondIndex Index des zweiten zu vertauschenden Elements
     */
    fun swap(firstIndex: Int, secondIndex: Int) {
        toIndex(firstIndex)
        val t1 = getContent()

        toIndex(secondIndex)
        val t2 = getContent()

        setContent(t1)

        toIndex(firstIndex)
        setContent(t2)
    }

    /**
     * Gibt das Objekt an der aktuellen Position des Pointers zurück
     *
     * @return Aktuelles Objekt.
     */
    fun getContent(): ContentType? {
        return if (this.hasAccess()) {
            current!!.content
        } else {
            null
        }
    }

    /**
     * Ersetzt das Objekt an der aktuellen Listenposition durch ein per Parameter Übergebenes.
     *
     * @param pContent Neues Listenobjekt
     */
    fun setContent(pContent: ContentType?) {
        if (pContent != null && this.hasAccess())
            current!!.content = pContent
    }

    /**
     * Liefert das Objekt nach dem Aktuellen, ohne den Pointer zu verschieben.
     *
     * @return Folgendes Listenobjekt.
     */
    fun getNext(): ContentType? {
        return if (hasAccess() && current!!.next != null) {
            current!!.next!!.content
        } else {
            null
        }
    }

    /**
     * Liefert das Objekt vor dem Aktuellen, ohne den Pointer zu verschieben.
     *
     * @return Vorheriges Listenobjekt.
     */
    fun getPrevious(): ContentType? {
        return if (hasAccess() && current!!.previous != null) {
            current!!.previous!!.content
        } else {
            null
        }
    }

    /**
     * Fügt ein neues Objekt vor die Position des Pointers ein, steht dieser auf dem null-Objekt ([.hasAccess] returns false), wird das Objekt ans
     * Ende der Liste angehängt. Der Pointer wird nicht verschoben.
     *
     * @param pContent Neues Listenobjekt
     */
    fun insertBefore(pContent: ContentType) {
        if (hasAccess()) {
            val newNode = Node(pContent)
            if (current === first)
                first = newNode
            newNode.insertBefore(current)
            length++
        } else {
            if (isEmpty()) {
                val newNode = Node(pContent)
                first = newNode
                last = newNode
                length = 1
            } else {
                append(pContent)
            }
        }
    }

    /**
     * Fügt ein neues Objekt hinter die Position des Pointers ein, steht dieser auf dem null-Objekt ([.hasAccess] returns false), wird das Objekt ans
     * Ende der Liste angehängt. Der Pointer wird nicht verschoben.
     *
     * @param pContent Neues Listenobjekt
     */
    fun insertBehind(pContent: ContentType) {
        if (hasAccess()) {
            val newNode = Node(pContent)
            newNode.insertBehind(current!!)
            length++
        } else {
            if (isEmpty()) {
                val newNode = Node(pContent)
                first = newNode
                last = newNode
                length = 1
            } else {
                append(pContent)
            }
        }
    }

    /**
     * Hängt ein neues Objekt ans Ende der Liste an ohne den Pointer zu verschieben.
     *
     * @param pContent Neues Listenobjekt
     * @return Instanz der geänderten Liste
     */
    fun append(pContent: ContentType): List<ContentType> {
        if (this.isEmpty()) {
            this.insertBefore(pContent)
        } else {
            val newNode = Node(pContent)
            newNode.insertBehind(last!!)
            last = newNode
            length++
        }
        return this
    }

    /**
     * Hängt eine andere Liste ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param pList Liste, die angehängt werden soll.
     * @return Instanz der geänderten Liste.
     */
    fun concat(pList: List<ContentType>): List<ContentType> {
        if (pList !== this && !pList.isEmpty()) {
            if (this.isEmpty()) {
                first = pList.first
                last = pList.last
            } else {
                last!!.next = pList.first
                pList.first!!.previous = last
                last = pList.last
            }
            pList.first = null
            pList.last = null
            pList.current = null
            length += pList.size()
        }
        return this
    }

    /**
     * Hängt ein Array ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param array Array, das angehängt werden soll.
     */
    fun concat(array: Array<ContentType>) {
        for (c in array)
            append(c)
    }

    /**
     * Hängt eine native java.util Liste ans Ende der aktuellen Liste an ohne den Pointer zu verschieben.
     *
     * @param pList [java.util.List], die angehängt werden soll.
     * @return Instanz der geänderten Liste.
     */
    fun concat(pList: kotlin.collections.List<ContentType>): List<ContentType> {
        for (t in pList) {
            append(t)
        }
        return this
    }

    /**
     * Entfernt das Objekt, auf das der Pointer zeigt. Der Pointer steht danach auf dem Nachfolgeelement.
     */
    fun remove() {
        if (this.hasAccess() && !this.isEmpty()) {
            val temp: Node = current as Node

            if (temp === first) {
                first = first!!.next
            } else {
                temp.previous!!.next = temp.next
            }

            if (temp === last) {
                last = temp.previous
            } else {
                temp.next!!.previous = temp.previous
            }
            next()

            temp.next = null
            temp.previous = null

            length--
        }
    }

    /**
     * Füllt ein Array desselben Datentyps mit dem Inhalt der Liste
     *
     * @param array Zu befüllendes Array
     * @return ""Gefülltes" Array
     */
    fun fill(array: Array<ContentType>): Array<ContentType> {
        toFirst()
        var i = 0
        while (i < array.size) {
            array[i] = getContent()!!
            i++
            next()
        }
        return array
    }

    /**
     * Liefert die Größe der Liste.
     *
     * @return Listengröße
     */
    fun size(): Int {
        return length
    }

    /**
     * Überprüft, ob die Liste ein bestimmtes Objekt enthält
     *
     * @param object Zu überprüfendes Objekt.
     * @return true, wenn Objekt Element der Liste.
     */
    operator fun contains(`object`: ContentType): Boolean {
        toFirst()
        while (hasAccess()) {
            if (`object` == getContent())
                return true
            next()
        }
        return false
    }

    /**
     * Liefert das Objekt an einem bestimmten Listenindex. Entspricht Aufrufen von [.toIndex] und anschließend [.getContent].
     *
     * @param index Listenindex
     * @return Listenobjekt
     */
    fun getObjectAt(index: Int): ContentType? {
        toIndex(index)
        return getContent()
    }

    /**
     * Gibt einen Iterator der Liste zurück.
     *
     * @return Iterator
     */
    @NonNull
    override operator fun iterator(): Iterator<ContentType> {
        current = null

        return object : Iterator<ContentType> {
            override fun hasNext(): Boolean {
                return current !== last
            }

            override fun next(): ContentType {
                if (hasAccess())
                    this@List.next()
                else
                    toFirst()
                return getContent()!!
            }
        }
    }

    private inner class Node internal constructor(internal var content: ContentType) {
        internal var next: Node?
        internal var previous: Node?

        init {
            next = null
            previous = null
        }

        internal fun insertBehind(pNode: Node) {
            this.next = pNode.next
            this.previous = pNode
            pNode.next = this

            if (this.next != null)
                this.next!!.previous = this
        }

        internal fun insertBefore(pNode: Node?) {
            this.previous = pNode!!.previous
            this.next = pNode
            pNode.previous = this

            if (this.previous != null)
                this.previous!!.next = this
        }
    }
}