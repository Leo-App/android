package de.slg.leoapp.timetable.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.timetable.data.db.DatabaseManager

class ParseTask : ObjectCallbackTask<Void?>() {
    override fun doInBackground(vararg params: Any?): Void? {
        //val klausuren = parser.parse()

        val db = params[1] as DatabaseManager
        //for (k in klausuren) {
        //db.databaseInterface().insertExam(k)
        //}

        return null
    }
}