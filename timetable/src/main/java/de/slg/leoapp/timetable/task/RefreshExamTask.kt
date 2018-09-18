package de.slg.leoapp.timetable.task

import de.slg.leoapp.core.task.ObjectCallbackTask

class RefreshExamTask : ObjectCallbackTask<Void?>() {
    override fun doInBackground(vararg params: Any): Void? {
//        val exam = (params[0] as DatabaseManager).databaseInterface().getExam(params[1] as Int)
//        val k = Klausur(
//                exam.id,
//                exam.fach,
//                exam.datum
//        )
//        return k
        return null
    }

//    override fun onPostExecute(result: Klausur) {
//        for (l: TaskStatusListener in listeners)
//            l.taskFinished(result)
//    }
}