package de.slg.leoapp.timetable.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.Lesson

@Database(entities = [Course::class, Lesson::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseManager : RoomDatabase() {

    companion object {
        private var instance: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager? {
            if (instance == null) {
                synchronized(DatabaseManager::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            DatabaseManager::class.java, "exams.db")
                            .build()
                }
            }
            return instance
        }
    }

    abstract fun databaseInterface(): DatabaseInterface
}