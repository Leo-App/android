package de.slg.leoapp.news.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.slg.leoapp.news.utils.Converters

@Database(entities = [Author::class, Entry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseManager : RoomDatabase() {

    abstract fun databaseInterface(): DatabaseInterface

    companion object {
        private var instance: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager? {
            if (instance == null) {
                synchronized(DatabaseManager::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            DatabaseManager::class.java, "news.db")
                            .build()
                }
            }
            return instance
        }
    }
}