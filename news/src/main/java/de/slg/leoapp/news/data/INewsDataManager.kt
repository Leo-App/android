package de.slg.leoapp.news.data

import android.content.Context
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import java.util.*

interface INewsDataManager {
    fun getCurrentEntries(callback: (List<Pair<Entry, Author>>) -> Unit)
    fun refreshEntries(userId: Int, context: Context, callback: () -> Unit = {})
    fun removeEntry(entry: Pair<Entry, Author>, context: Context, callback: (Boolean) -> Unit = {})
    fun updateEntry(entry: Pair<Entry, Author>, context: Context, callback: (Boolean) -> Unit = {})
    fun addEntry(author: Int, title: String, content: String, recipient: String, deadline: Date, context: Context, callback: (Boolean) -> Unit = {})
}