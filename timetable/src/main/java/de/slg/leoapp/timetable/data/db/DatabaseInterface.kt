package de.slg.leoapp.timetable.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.Lesson
import de.slg.leoapp.timetable.data.UILesson

@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM courses WHERE grade = :grade")
    fun getCourses(grade: String): Array<Course>

    @Query("SELECT DISTINCT courses.* FROM lessons, courses WHERE day * 100 + hour IN (SELECT day * 100 + hour FROM lessons WHERE courseId = :id) AND courseId != :id AND id = courseId AND grade = :grade ORDER BY type DESC, subject")
    fun getIntersectingCourses(id: Long, grade: String): Array<Course>

    @Query("SELECT day, hour, room, subject, teacher FROM courses, lessons WHERE chosen AND id = courseId AND day = :day ORDER BY hour")
    fun getUILessons(day: Int): List<UILesson>

    @Insert(onConflict = ABORT)
    fun insertCourse(course: Course): Long

    @Insert(onConflict = REPLACE)
    fun insertLesson(lesson: Lesson)

    @Query("UPDATE courses SET chosen = 1 WHERE id IN (:ids)")
    fun choseCourses(ids: Array<Long>)

}