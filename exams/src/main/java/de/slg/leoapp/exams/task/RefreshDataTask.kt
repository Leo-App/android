package de.slg.leoapp.exams.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.exams.Klausur
import de.slg.leoapp.exams.data.db.DatabaseManager

class RefreshDataTask : ObjectCallbackTask<Array<Klausur>>() {
    override fun doInBackground(vararg params: Any): Array<Klausur> {
        val array = (params[0] as DatabaseManager).databaseInterface().getDownloadedExams()
        return Array(array.size) { i -> Klausur(array[i].id, array[i].fach, array[i].datum) }
    }

    override fun onPostExecute(result: Array<Klausur>) {
        for (l: TaskStatusListener in listeners)
            l.taskFinished(result)
    }
}