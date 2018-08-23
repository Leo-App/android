package de.leoappslg.news.data

import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry

interface INewsDataManager {
    fun getCurrentEntries() : List<Pair<Entry, Author>>
    fun refreshEntries()
}