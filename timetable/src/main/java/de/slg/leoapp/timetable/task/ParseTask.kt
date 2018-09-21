package de.slg.leoapp.timetable.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.Lesson
import de.slg.leoapp.timetable.data.LessonTime
import de.slg.leoapp.timetable.data.db.Converters
import de.slg.leoapp.timetable.data.db.DatabaseManager
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ParseTask : ObjectCallbackTask<Void?>() {

    private var lastCourseName = ""
    private var lastId: Long = 0

    override fun doInBackground(vararg params: Any?): Void? {
        val db = params[1] as DatabaseManager

        val lines = BufferedReader(InputStreamReader(params[0] as InputStream)).readLines()

        val converter = Converters()

        for (line in lines) {
            val parts = line.substring(line.indexOf(',') + 1).replace("\"", "").split(",")
            val grade = parts[0]
            val teacher = parts[1]
            val courseName = parts[2]
            val room = parts[3]
            val day = parts[4]
            val hour = parts[5]

            if (grade.isBlank() || teacher.isBlank() || courseName.isBlank() || room.isBlank() || day.isBlank() || hour.isBlank())
                continue
            if (courseName == "Vertr")
                continue

            if (lastCourseName != courseName) {
                val subject = converter.toSubject(when {
                    grade.matches(Regex("\\d\\d[abcf]")) -> courseName
                    courseName.startsWith("IB") -> courseName
                    courseName.contains('-') -> courseName.substring(0, courseName.indexOf('-'))
                    courseName.contains(' ') -> courseName.substring(0, courseName.indexOf(' '))
                    courseName.contains(Regex("G\\d")) -> courseName.substring(0, courseName.lastIndexOf("G"))
                    courseName.contains(Regex("L\\d")) -> courseName.substring(0, courseName.lastIndexOf("L"))
                    else -> ""
                })
                val type = when {
                    courseName.matches(Regex("..L\\d")) -> "LK"
                    courseName.startsWith("AG") -> "AG"
                    else -> "GK"
                }

                lastId = db.databaseInterface().insertCourse(Course(null, subject, courseName, type, teacher, grade, false, false))
            }

            db.databaseInterface().insertLesson(Lesson(lastId, LessonTime(Integer.parseInt(day), Integer.parseInt(hour)), room))

            lastCourseName = courseName

            //println("grade: $grade, teacher: $teacher, course: $course, room: $room, day: $day, hour: $hour")
        }

        return null
    }
}