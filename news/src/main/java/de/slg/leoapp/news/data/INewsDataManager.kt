package de.slg.leoapp.news.data

import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry

interface INewsDataManager {
    fun getCurrentEntries() : List<Pair<Entry, Author>>
    fun refreshEntries()
}