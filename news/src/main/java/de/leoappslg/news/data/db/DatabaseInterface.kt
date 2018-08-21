package de.leoappslg.news.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface DatabaseInterface {

    @Query("SELECT * FROM entries")
    fun getEntries(): List<Entry>

    @Query("SELECT * FROM authors WHERE id=:entryId LIMIT 1")
    fun getAuthor(entryId: Int): Author

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(author: Author)

}