package de.slg.leoapp.exams.data.db

import androidx.room.TypeConverter
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 07.09.2018
 */
class Converters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
}