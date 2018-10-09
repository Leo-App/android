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

    @Query("SELECT * FROM courses")
    fun getCourses(): Array<Course>

    @Query("SELECT * FROM courses WHERE grade = :grade AND id IN (SELECT DISTINCT courseId FROM lessons WHERE day + '.' + hour NOT IN (SELECT day + '.' + hour FROM lessons WHERE courseId IN (:taken)))")
    fun getPossibleCourses(taken: List<Long>, grade: String): Array<Course>

    @Query("SELECT day, hour, room, subject, teacher FROM courses, lessons WHERE chosen AND id = courseId AND day = :day")
    fun getUILessons(day: String): List<UILesson>

    @Insert(onConflict = ABORT)
    fun insertCourse(course: Course): Long

    @Insert(onConflict = REPLACE)
    fun insertLesson(lesson: Lesson)

}