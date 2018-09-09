package de.slg.leoapp.exams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 08.09.2018
 */
@Entity(tableName = "exams_created")
data class CreatedExam(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "date") val datum: Date,
        @ColumnInfo(name = "subject") val fach: String
)