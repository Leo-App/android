package de.slg.leoapp.exams.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.parser.XMLParser
import java.io.InputStream

class ParseTask : ObjectCallbackTask<Void?>() {
    override fun doInBackground(vararg params: Any?): Void? {
        val parser = XMLParser(params[0] as InputStream)
        val klausuren = parser.parse()

        val db = params[1] as DatabaseManager
        for (k in klausuren) {
            db.databaseInterface().insertExam(k)
        }

        return null
    }
}