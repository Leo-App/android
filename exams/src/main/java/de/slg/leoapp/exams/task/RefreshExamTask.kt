package de.slg.leoapp.exams.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.exams.Klausur
import de.slg.leoapp.exams.data.db.DatabaseManager

class RefreshExamTask : ObjectCallbackTask<Klausur>() {
    override fun doInBackground(vararg params: Any): Klausur {
        val exam = (params[0] as DatabaseManager).databaseInterface().getExam(params[1] as Int)
        val k = Klausur(
                exam.id,
                exam.fach,
                exam.datum
        )
        return k
    }

    override fun onPostExecute(result: Klausur) {
        for (l: TaskStatusListener in listeners)
            l.taskFinished(result)
    }
}