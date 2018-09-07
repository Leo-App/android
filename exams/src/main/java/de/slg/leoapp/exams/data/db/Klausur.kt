package de.slg.leoapp.exams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 06.09.2018
 */
@Entity(tableName = "exams")
data class Klausur(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "date") val datum: Date,
        @ColumnInfo(name = "text") val title: String,
        @ColumnInfo(name = "subject") val fach: String,
        @ColumnInfo(name = "downloaded") val d: Boolean
)