@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.leoapp_slg.core.datastructure

import androidx.annotation.NonNull

/**
 * Dynamisch lineare Datenstruktur, die immer das als erstes eingefügte Element liefert und
 * Elemente in einer Schlange aufreiht.
 *
 * @param <ContentType> Inhaltsdatentyp
 * @author Moritz
 * @version 2017.2312
 * @since 0.7.1
</ContentType> */
class Queue<ContentType>() : Iterable<ContentType> {

    private var first: Node? = null
    private var last: Node? = null
    private var size: Int = 0

    init {
        first = null
        last = null
        size = 0
    }

    constructor(objects: Array<ContentType>) : this() {
        for (pContent in objects)
            append(pContent)
    }

    fun getContent(): ContentType? {
        return if (!isEmpty()) {
            first!!.content
        } else {
            null
        }
    }

    fun isEmpty(): Boolean {
        return first == null
    }

    fun size(): Int {
        return size
    }

    fun remove(): Queue<ContentType> {
        if (!isEmpty()) {
            this.first = first!!.next
            size--
        }

        return this
    }

    fun append(pContent: ContentType): Queue<ContentType> {
        val n = Node(pContent)
        if (isEmpty()) {
            first = n
        } else {
            last!!.next = n
        }
        last = n
        size++

        return this
    }

    @NonNull
    override fun iterator(): Iterator<ContentType> {
        return object : Iterator<ContentType> {
            override fun hasNext(): Boolean {
                return first!!.next == null
            }

            override fun next(): ContentType {
                return this@Queue.remove().getContent()!!
            }
        }
    }

    fun clear(): Queue<ContentType> {
        first = null
        last = null
        size = 0
        return this
    }

    private inner class Node(internal val content: ContentType) {
        internal var next: Node? = null
    }
}