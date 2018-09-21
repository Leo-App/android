package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo

data class UILesson(
        @ColumnInfo(name = "time") val time: LessonTime,
        @ColumnInfo(name = "room") val room: String,
        @ColumnInfo(name = "subject") val subject: Subject,
        @ColumnInfo(name = "teacher") val teacher: String
)