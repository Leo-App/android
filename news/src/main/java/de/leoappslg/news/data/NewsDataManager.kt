package de.leoappslg.news.data

import de.leoappslg.news.data.db.Entry

object NewsDataManager : INewsDataManager {

    override fun getCurrentEntries() : List<Entry> {
        return emptyList() //TODO change
    }

}