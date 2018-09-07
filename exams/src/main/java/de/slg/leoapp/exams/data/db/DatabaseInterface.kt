package de.slg.leoapp.exams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @author Moritz
 * Erstelldatum: 06.09.2018
 */
@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM exams")
    fun getExams(): List<Klausur>

    @Query("SELECT * FROM exams WHERE downloaded = 1")
    fun getDownloadedExams(): List<Klausur>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(klausur: Klausur)
}