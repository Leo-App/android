package de.slg.leoapp.news.data

import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.DatabaseManager
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.data.http.ApiConnector
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

object NewsDataManager : INewsDataManager {

    private lateinit var databaseManager: DatabaseManager

    init {
        refreshEntries()
    }

    override fun getCurrentEntries(): List<Pair<Entry, Author>> {
        if (!::databaseManager.isInitialized)
            return emptyList()

        val databaseInterface = databaseManager.databaseInterface()
        val entries = databaseManager.databaseInterface().getEntries()
        val listing = mutableListOf<Pair<Entry, Author>>()

        for (cur in entries) {
            listing.add(Pair(cur, databaseInterface.getAuthor(cur.authorId)))
        }

        return listing
    }

    override fun refreshEntries() {
        launch(CommonPool) {
            val data = ApiConnector.synchronizeNews()
            for (cur in data) {
                databaseManager.databaseInterface().insert(cur.first)
                databaseManager.databaseInterface().insert(cur.second)
            }
        }
    }

    fun setDatabaseManager(manager: DatabaseManager?) {
        manager ?: return
        databaseManager = manager
    }

}