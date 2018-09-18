package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
class Course(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "subject") val subject: Subject,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "teacher") val teacher: String,
        @ColumnInfo(name = "grade") val grade: String,
        @ColumnInfo(name = "choosen") val choosen: Boolean,
        @ColumnInfo(name = "exams") val exams: Boolean
)