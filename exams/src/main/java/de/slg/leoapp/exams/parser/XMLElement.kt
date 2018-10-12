package de.slg.leoapp.exams.parser

import de.slg.leoapp.core.datastructure.List

internal class XMLElement(val tag: String) {
    val content: StringBuilder = StringBuilder()
    val children: List<XMLElement> = List()

    override fun toString(): String {
        return "$tag: $content"
    }

    fun isEmpty(): Boolean {
        return content.replace(Regex("\\W"), "").isEmpty()
    }
}