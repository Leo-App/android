@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.slg.leoapp.core.datastructure

import androidx.annotation.NonNull
import de.slg.leoapp.core.datastructure.exception.NodeIndexOutOfBoundsException

/**
 * Nicht lineare Datenstruktur, bei der Elemente in einer Baumstruktur angeordnet sind. Dabei hat
 * ist jedes Element "Wurzel" für zwei weitere, untergeordnete Elemente
 *
 * @author Gianni
 * @version 2017.2912
 * @since 0.7.2
 */
class BinaryTree<ContentType>() : Iterable<ContentType> {

    private var node: BTNode? = null

    constructor(pContent: ContentType) : this() {
        this.node = BTNode(pContent)
    }

    constructor(pContent: ContentType, pLeftTree: BinaryTree<ContentType>?, pRightTree: BinaryTree<ContentType>?) : this() {
        this.node = BTNode(pContent)
        if (pLeftTree != null) {
            this.node!!.left = pLeftTree
        }
        if (pRightTree != null) {
            this.node!!.right = pRightTree
        }
    }

    var content: ContentType?
        get() {
            return if (!this.isEmpty()) {
                this.node!!.content
            } else {
                null
            }
        }
        set(value) {
            if (this.isEmpty()) {
                node = BTNode(value!!)
            } else {
                this.node!!.content = value!!
            }
        }

    var leftTree: BinaryTree<ContentType>?
        get() {
            return if (!isEmpty()) {
                node!!.left
            } else {
                null
            }
        }
        set(value) {
            if (!isEmpty()) {
                this.node!!.left = value!!
            }
        }


    var rightTree: BinaryTree<ContentType>?
        get() {
            return if (!isEmpty()) {
                node!!.right
            } else {
                null
            }
        }
        set(value) {
            if (!isEmpty()) {
                this.node!!.right = value!!
            }
        }

    fun isEmpty(): Boolean {
        return this.node == null
    }

    //preorder
    @NonNull
    override fun iterator(): Iterator<ContentType> {
        return object : Iterator<ContentType> {
            override fun hasNext(): Boolean {
                var current: BinaryTree<ContentType> = this@BinaryTree
                while (true) {
                    current = if (!current.rightTree!!.isEmpty())
                        current.rightTree!!
                    else if (!current.leftTree!!.isEmpty())
                        current.leftTree!!
                    else
                        return current.node!!.marked
                }
            }

            @Throws(NodeIndexOutOfBoundsException::class)
            override fun next(): ContentType {
                this@BinaryTree.node!!.marked = true

                return when {
                    leftTree!!.iterator().hasNext() -> leftTree!!.iterator().next()
                    rightTree!!.iterator().hasNext() -> rightTree!!.iterator().next()
                    else -> throw NodeIndexOutOfBoundsException("No new nodes available")
                }

            }

        }
    }

    //preorder
    override fun toString(): String {
        return toString(0)
    }

    private fun toString(level: Int): String {
        var l = level
        if (isEmpty())
            return ""

        val toString = StringBuilder(content!!.toString())
                .append("_;;_")

        l++

        if (!leftTree!!.isEmpty())
            toString.append(";L").append(l).append(";").append(leftTree!!.toString(l))
        if (!rightTree!!.isEmpty()) {
            toString.append(";R").append(l).append(";").append(rightTree!!.toString(l))
        }

        return toString.toString()
    }

    private inner class BTNode(internal var content: ContentType) {
        internal var left: BinaryTree<ContentType> = BinaryTree()
        internal var right: BinaryTree<ContentType> = BinaryTree()
        internal var marked: Boolean = false
    }
}