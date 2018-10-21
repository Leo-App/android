package de.slg.leoapp.exams.data.db

import androidx.room.*

@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM exams")
    fun getExams(): Array<Exam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(exam: Exam)

    @Query("SELECT * FROM exams WHERE id = :id")
    fun getExam(id: Int): Exam

    @Update
    fun updateExam(exam: Exam)
}