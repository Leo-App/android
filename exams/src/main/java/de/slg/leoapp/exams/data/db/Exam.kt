package de.slg.leoapp.exams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "exams")
data class Exam(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "date") val datum: Date,
        @ColumnInfo(name = "subject") val fach: Subject,
        @ColumnInfo(name = "course") val kurs: String?,
        @ColumnInfo(name = "teacher") val lehrer: String?,
        @ColumnInfo(name = "grade") val stufe: String?
)