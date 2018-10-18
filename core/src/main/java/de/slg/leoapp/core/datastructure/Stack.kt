@file:Suppress("unused")

package de.slg.leoapp.core.datastructure

class Stack<ContentType> {
    private var length: Int = 0
    private var top: Node? = null

    fun add(content: ContentType): Stack<ContentType> {
        top = Node(content, top)
        length++
        return this
    }

    fun remove(): Stack<ContentType> {
        if (!isEmpty()) {
            top = top!!.next
            length--
        }
        return this
    }

    fun getContent(): ContentType? {
        return if (!isEmpty()) {
            top!!.content
        } else {
            null
        }
    }

    fun getLength(): Int {
        return length
    }

    fun isEmpty(): Boolean {
        return top == null
    }

    private inner class Node(internal var content: ContentType, internal var next: Node?)
}