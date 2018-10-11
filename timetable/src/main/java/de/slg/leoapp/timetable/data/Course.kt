package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
        @PrimaryKey(autoGenerate = true) val id: Long?,
        @ColumnInfo(name = "subject") val subject: Subject,
        @ColumnInfo(name = "number") val number: Int,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "teacher") val teacher: String,
        @ColumnInfo(name = "grade") val grade: String,
        @ColumnInfo(name = "chosen") val choosen: Boolean,
        @ColumnInfo(name = "exams") val exams: Boolean
)