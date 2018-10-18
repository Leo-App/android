package de.slg.leoapp.exams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM exams_downloaded")
    fun getExams(): Array<DownloadedExam>

    @Query("SELECT * FROM exams_downloaded")
    fun getDownloadedExams(): Array<DownloadedExam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(downloadedExam: DownloadedExam)

    @Query("SELECT * FROM exams_downloaded WHERE id = :id")
    fun getExam(id: Int): DownloadedExam
}