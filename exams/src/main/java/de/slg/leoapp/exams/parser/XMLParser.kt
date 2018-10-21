package de.slg.leoapp.exams.parser

import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.datastructure.Stack
import de.slg.leoapp.exams.data.db.Converters
import de.slg.leoapp.exams.data.db.Exam
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory

class XMLParser(private val input: InputStream) {

    private val cellType = List(
            "wochentag",
            "date",
            "ef",
            "q1",
            "q2"
    )

    private val slgPatterns = arrayOf(
            Pattern.compile("[A-Za-z]{1,3} G[K\\d] [A-ZÄÖÜ]{3}"), // GeF G3 TAS, D G2 SHM, EK GK HEU
            Pattern.compile("[A-Ze]{1,3} [A-ZÄÖÜ]{3}"), // GeF TAS
            Pattern.compile("[A-Z] ?\\d ?[A-Z] ?\\d [A-ZÄÖÜ]{3}"), // S6G1 GOM, L8G2 BEH
            Pattern.compile(" [A-Za-z]{1,2} G\\d \\(\\d+\\)"), // IF G1 (6)
            Pattern.compile(" [A-Za-z]{1,2} \\(\\d+\\)") // EK (6)
    )

    private val klausuren: List<Exam> = List()

    private var currentDate: Calendar = GregorianCalendar()

    private var foundYear = false

    private val converters = Converters()

    fun parse(): List<Exam> {
        currentDate.set(Calendar.MONTH, Calendar.JANUARY)

        val factory = SAXParserFactory.newInstance()

        var root = XMLElement("")
        val current: Stack<XMLElement> = Stack()

        val parser = factory.newSAXParser()
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

        for (t in root.children) {
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
        for (t in tree.children) {
            if (t.tag == "tgroup") {
                tgroup(t)
            }
        }
    }

    private fun tgroup(tree: XMLElement) {
        for (t in tree.children) {
            if (t.tag == "tbody") {
                tbody(t)
            }
        }
    }

    private fun tbody(tree: XMLElement) {
        var b = false
        for (t in tree.children) {
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
        for (t in tree.children) {
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

        for (t in tree.children) {
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
            "date" -> {
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
                var matcher = slgPatterns[0].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group()
                    val i1 = result.indexOf(' ')
                    val i2 = result.indexOf(' ', i1 + 1)
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result.substring(0, i1)),
                                    result.substring(i1 + 1, i2),
                                    result.substring(i2 + 1),
                                    cellType.getContent()
                            )
                    )
                }
                matcher = slgPatterns[1].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group()
                    if (!result.startsWith("GK")) {
                        val i1 = result.indexOf(' ')
                        klausuren.append(
                                Exam(
                                        null,
                                        currentDate.time,
                                        converters.toSubject(result.substring(0, i1)),
                                        "LK",
                                        result.substring(i1 + 1),
                                        cellType.getContent()
                                )
                        )
                    }
                }
                matcher = slgPatterns[2].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group().replace(" ", "")
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result.substring(0, 2)),
                                    result.substring(2, 4),
                                    result.substring(4),
                                    cellType.getContent()
                            )
                    )
                }
            }
            "q1", "q2" -> {
                var matcher = slgPatterns[0].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group()
                    val i1 = result.indexOf(' ')
                    val i2 = result.indexOf(' ', i1 + 1)
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result.substring(0, i1)),
                                    result.substring(i1 + 1, i2),
                                    result.substring(i2 + 1),
                                    cellType.getContent()
                            )
                    )
                }
                matcher = slgPatterns[1].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group()
                    if (!result.startsWith("GK")) {
                        val i1 = result.indexOf(' ')
                        klausuren.append(
                                Exam(
                                        null,
                                        currentDate.time,
                                        converters.toSubject(result.substring(0, i1)),
                                        "LK",
                                        result.substring(i1 + 1),
                                        cellType.getContent()
                                )
                        )
                    }
                }
                matcher = slgPatterns[2].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group().replace(" ", "")
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result.substring(0, 2)),
                                    result.substring(2, 4),
                                    result.substring(4),
                                    cellType.getContent()
                            )
                    )
                }
                matcher = slgPatterns[3].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group().substring(1).replace(Regex(" \\(\\d+\\)"), "")
                    val i1 = result.indexOf(' ')
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result.substring(0, i1)),
                                    result.substring(i1 + 1),
                                    "koop",
                                    cellType.getContent()
                            )
                    )
                }
                matcher = slgPatterns[4].matcher(content)
                while (matcher.find()) {
                    val result = matcher.group().substring(1).replace(Regex(" \\(\\d+\\)"), "")
                    klausuren.append(
                            Exam(
                                    null,
                                    currentDate.time,
                                    converters.toSubject(result),
                                    "LK",
                                    "koop",
                                    cellType.getContent()
                            )
                    )
                }
            }
        }
    }
}