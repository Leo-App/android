package de.slg.leoapp.news.data.db

import androidx.room.*

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

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateEntry(entry: Entry)

    @Delete
    fun removeEntry(entry: Entry)

}