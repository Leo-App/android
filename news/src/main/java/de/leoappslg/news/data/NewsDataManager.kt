package de.leoappslg.news.data

import de.leoappslg.news.data.db.DatabaseManager
import de.leoappslg.news.data.db.Entry

object NewsDataManager : INewsDataManager {

    private lateinit var databaseManager: DatabaseManager

    init {
        synchronizeNews()
    }

    override fun getCurrentEntries() : List<Entry> {
        if (!::databaseManager.isInitialized)
            return emptyList()
        return emptyList() //TODO implement
    }

    fun setDatabaseManager(manager: DatabaseManager?) {
        manager ?: return
        databaseManager = manager
    }

    private fun synchronizeNews() {

    }

}