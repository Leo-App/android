package de.slg.leoapp.timetable.data

import androidx.room.ColumnInfo

data class UILesson(
        @ColumnInfo(name = "day") val day: Int,
        @ColumnInfo(name = "hour") val hour: Int,
        @ColumnInfo(name = "room") val room: String,
        @ColumnInfo(name = "subject") val subject: Subject,
        @ColumnInfo(name = "teacher") val teacher: String
)