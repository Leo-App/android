package de.leoappslg.news.data

import de.leoappslg.news.data.db.Entry

interface INewsDataManager {
    fun getCurrentEntries() : List<Entry>
}