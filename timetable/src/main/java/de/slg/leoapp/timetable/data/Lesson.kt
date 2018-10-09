package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "lessons", primaryKeys = ["courseId", "day", "hour"])
data class Lesson(
        @ForeignKey(entity = Course::class, parentColumns = ["id"], childColumns = ["courseId"], onDelete = CASCADE) @ColumnInfo(name = "courseId") val id: Long,
        @ColumnInfo(name = "day") val day: Int,
        @ColumnInfo(name = "hour") val hour: Int,
        @ColumnInfo(name = "room") val room: String
)