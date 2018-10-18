package de.slg.leoapp.exams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "exams_created")
data class CreatedExam(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "date") val datum: Date,
        @ColumnInfo(name = "subject") val fach: String
)