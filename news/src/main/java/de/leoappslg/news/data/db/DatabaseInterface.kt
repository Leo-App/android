package de.leoappslg.news.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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