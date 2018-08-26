package de.leoappslg.news.utils

import androidx.room.TypeConverter
import de.slg.leoapp.core.data.ProfilePicture
import java.util.*

class Converters {
    @TypeConverter
    fun fromProfilePicture(picture: ProfilePicture): String {
        return picture.getURLString()
    }

    @TypeConverter
    fun toProfilePicture(picture: String): ProfilePicture {
        return ProfilePicture(picture)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

}