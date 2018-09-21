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

    @Query("SELECT * FROM courses WHERE id IN (SELECT DISTINCT courseId FROM lessons WHERE time NOT IN (SELECT time FROM lessons WHERE courseId IN (:taken)))")
    fun getPossibleCourses(taken: Array<Int>): Array<Course>

    @Query("SELECT time, room, subject, teacher FROM courses, lessons WHERE chosen = 1 AND id = courseId AND time LIKE :day")
    fun getUILessons(day: String): Array<UILesson>

    @Insert(onConflict = ABORT)
    fun insertCourse(course: Course): Long

    @Insert(onConflict = REPLACE)
    fun insertLesson(lesson: Lesson)

}