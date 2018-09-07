package de.slg.leoapp.exams.xml

import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.datastructure.Stack
import de.slg.leoapp.exams.Klausur
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

/**
 * @author Moritz
 * Erstelldatum: 05.09.2018
 */
class XMLParser(private val input: InputStream) {

    private val cellType: List<String> = List(
            "wochentag",
            "datum",
            "ef",
            "q1",
            "q2"
    )

    private val slgPatterns = List(
            Pattern.compile("[A-Za-z]{1,3} G[K\\d] [A-ZÄÖÜ]{3}"), // GeF G3 TAS, D G2 SHM, EK GK HEU
            Pattern.compile("[A-Ze]{1,3} [A-ZÄÖÜ]{3}"), // GeF TAS
            Pattern.compile("[A-Z]\\d[A-Z]\\d [A-ZÄÖÜ]{3}") // S6G1 GOM, L8G2 BEH
    )

    private val koopPatterns = List(
            Pattern.compile("KKG:.+[A-Za-z]{1,3}"), // KKG: EK G1 (3), IF
            Pattern.compile("COU:.+[A-Za-z]{1,3}") // COU: EK (6), KU
    )

    private val klausuren: List<Klausur> = List()

    private var currentDate: Calendar = GregorianCalendar()

    private var foundYear = false

    fun parse(): List<Klausur> {
        currentDate.set(Calendar.MONTH, Calendar.JANUARY)

        val factory = SAXParserFactory.newInstance()
        factory.isValidating = true

        var root = XMLElement("")
        val current: Stack<XMLElement> = Stack()

        val parser: SAXParser = factory.newSAXParser()
        parser.parse(input, object : DefaultHandler() {
            override fun startElement(uri: String?, localName: String?, tag: String?, attributes: Attributes?) {
                if (!current.isEmpty()) {
                    val tree = XMLElement(tag!!)
                    current.getContent()!!.children.append(tree)
                    current.add(tree)
                } else {
                    root = XMLElement(tag!!)
                    current.add(root)
                }
            }

            override fun endElement(uri: String?, localName: String?, tag: String?) {
                current.remove()
            }

            override fun characters(ch: CharArray?, start: Int, length: Int) {
                val builder = current.getContent()!!.content
                for (i in start until start + length) {
                    val c = ch!![i]
                    if (c.toInt() != 160 && (!c.isWhitespace() || !builder.isEmpty() && !builder.last().isWhitespace()))
                        builder.append(c)
                }
            }
        })

        for (t: XMLElement in root.children) {
            if (t.tag == "para" && !t.isEmpty()) {
                findYear(t.content.toString())
            } else if (t.tag == "informaltable") {
                informaltable(t)
            }
        }

        return klausuren
    }

    private fun findYear(content: String) {
        println("foundYear? $content")

        if (foundYear) {
            val matcher = Pattern.compile("20\\d\\d").matcher(content)
            if (matcher.find()) {
                foundYear = true

                currentDate.set(Calendar.YEAR, Integer.parseInt(matcher.group()))
            }
        }
    }

    private fun informaltable(tree: XMLElement) {
        for (t: XMLElement in tree.children) {
            if (t.tag == "tgroup") {
                tgroup(t)
            }
        }
    }

    private fun tgroup(tree: XMLElement) {
        for (t: XMLElement in tree.children) {
            if (t.tag == "tbody") {
                tbody(t)
            }
        }
    }

    private fun tbody(tree: XMLElement) {
        var b = false
        for (t: XMLElement in tree.children) {
            if (!b && t.tag == "row") {
                b = true
                continue
            }
            if (t.tag == "row") {
                row(t)
            }
        }
    }

    private fun row(tree: XMLElement) {
        cellType.toFirst()
        for (t: XMLElement in tree.children) {
            if (cellType.getContent() == "wochentag") {
                cellType.next()
                continue
            }
            if (t.tag == "entry") {
                entry(t)
                cellType.next()
            }
        }
    }

    private fun entry(tree: XMLElement) {
        var b = false
        val builder = StringBuilder()

        for (t: XMLElement in tree.children) {
            if (t.tag == "para") {
                builder.append(t.content.toString().replace(Regex("\\s+"), " "))
                if (b)
                    builder.append('\n')
                b = true
            }
        }

        if (b) {
            para(builder.toString())
        }
    }

    private fun para(content: String) {
        when (cellType.getContent()) {
            "datum" -> {
                println(content)
                var day = 0
                var month = 0

                val matcher = Pattern.compile("\\d\\d").matcher(content)
                if (matcher.find()) {
                    day = Integer.parseInt(matcher.group())
                }
                if (matcher.find()) {
                    month = Integer.parseInt(matcher.group()) - 1
                }

                if (month < currentDate.get(Calendar.MONTH)) {
                    currentDate.add(Calendar.YEAR, 1)
                }

                currentDate.set(Calendar.MONTH, month)
                currentDate.set(Calendar.DAY_OF_MONTH, day)
            }
            "ef" -> {
                println("ef")
                for (pattern in slgPatterns) {
                    val matcher = pattern.matcher(content)
                    while (matcher.find()) {
                        val result = matcher.group()
                        if (!result.startsWith("GK")) {
                            println(result)
                            klausuren.append(Klausur(result, currentDate.time, 0))
                        }
                    }
                }
            }
            "q1" -> {
                println("q1")
                for (pattern in slgPatterns) {
                    val matcher = pattern.matcher(content)
                    while (matcher.find()) {
                        val result = matcher.group()
                        if (!result.contains("KKG") && !result.contains("COU") && !result.startsWith("GK") && !result.startsWith("GK") && !result.startsWith("LK")) {
                            println(result)
                            klausuren.append(Klausur(result, currentDate.time, 0))
                        }
                    }
                }
                for (pattern in koopPatterns) {
                    val matcher = pattern.matcher(content)
                    while (matcher.find()) {
                        val result = matcher.group().substring(matcher.group().indexOf(':') + 1).replace(Regex("\\(\\d{1,2}\\)"), "").replace(Regex("\\s"), "")
                        println(result)
                    }
                }
            }
            "q2" -> {
                println("q2")
                for (pattern in slgPatterns) {
                    val matcher = pattern.matcher(content)
                    while (matcher.find()) {
                        val result = matcher.group()
                        if (!result.contains("KKG") && !result.contains("COU") && !result.startsWith("GK") && !result.startsWith("LK")) {
                            println(result)
                            klausuren.append(Klausur(result, currentDate.time, 0))
                        }
                    }
                }
                for (pattern in koopPatterns) {
                    val matcher = pattern.matcher(content)
                    while (matcher.find()) {
                        val result = matcher.group().substring(matcher.group().indexOf(':') + 1).replace(Regex("\\(\\d{1,2}\\)"), "").replace(Regex("\\s"), "")
                        println(result)
                    }
                }
            }
        }
    }
}