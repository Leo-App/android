package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "lessons", primaryKeys = ["courseId", "time"])
data class Lesson(
        @ForeignKey(entity = Course::class, parentColumns = ["id"], childColumns = ["courseId"], onDelete = CASCADE) @ColumnInfo(name = "courseId") val id: Long,
        @ColumnInfo(name = "time") val time: LessonTime,
        @ColumnInfo(name = "room") val room: String
)