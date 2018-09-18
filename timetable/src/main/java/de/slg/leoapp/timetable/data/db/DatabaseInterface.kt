package de.slg.leoapp.timetable.data.db

import androidx.room.Dao
import androidx.room.Query
import de.slg.leoapp.timetable.data.Course

@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM courses")
    fun getCourses(): Array<Course>

    @Query("SELECT * FROM courses WHERE id IN (SELECT DISTINCT courseId FROM lessons WHERE time NOT IN (SELECT time FROM lessons WHERE courseId IN (:taken)))")
    fun getPossibleCourses(taken: Array<Int>): Array<Course>

}