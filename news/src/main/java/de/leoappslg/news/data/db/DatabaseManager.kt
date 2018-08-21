package de.leoappslg.news.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import de.leoappslg.news.utils.Converters

@Database(entities = [Author::class, Entry::class], version = 1)
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