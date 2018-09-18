package de.slg.leoapp.timetable.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.db.DatabaseManager

class RefreshDataTask : ObjectCallbackTask<Array<Course>>() {
    override fun doInBackground(vararg params: Any): Array<Course> {
        return (params[0] as DatabaseManager).databaseInterface().getCourses()
    }

    override fun onPostExecute(result: Array<Course>) {
        for (l: TaskStatusListener in listeners)
            l.taskFinished(result)
    }
}