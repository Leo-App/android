package de.slg.leoapp.exams.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * @author Moritz
 * Erstelldatum: 06.09.2018
 */
@Database(entities = [Klausur::class], version = 1, exportSchema = false)
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