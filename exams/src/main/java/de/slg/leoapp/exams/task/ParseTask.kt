package de.slg.leoapp.exams.task

import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.exams.Klausur
import de.slg.leoapp.exams.xml.XMLParser
import java.io.InputStream

/**
 * @author Moritz
 * Erstelldatum: 07.09.2018
 */
class ParseTask : ObjectCallbackTask<List<Klausur>>() {
    override fun doInBackground(vararg params: Any?): List<Klausur> {
        val parser = XMLParser(params[0] as InputStream)
        return parser.parse()
    }
}