package de.slg.leoapp.news.data

import android.content.Context
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import java.util.*

interface INewsDataManager {
    fun getCurrentEntries() : List<Pair<Entry, Author>>
    fun refreshEntries(callback: () -> Unit = {})
    fun removeEntry(entry: Pair<Entry, Author>)
    fun updateEntry(entry: Pair<Entry, Author>)
    fun addEntry(title: String, content: String, recipient: String, deadline: Date)

    //Initialization
    fun initApiKey(context: Context)
}