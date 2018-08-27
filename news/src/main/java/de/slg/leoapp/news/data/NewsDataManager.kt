package de.slg.leoapp.news.data

import android.content.Context
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.DatabaseManager
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.data.http.ApiConnector
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*
//todo decide if waiting times are handled in the presenters or via callbacks, (i tend towards callbacks)
object NewsDataManager : INewsDataManager {

    private lateinit var databaseManager: DatabaseManager

    override fun initApiKey(context: Context) {
        ApiConnector.apiKey = Utils.Network.getAPIKey(context)
    }

    override fun getCurrentEntries(): List<Pair<Entry, Author>> {
        if (!::databaseManager.isInitialized)
            return emptyList()

        val databaseInterface = databaseManager.databaseInterface()
        val entries = databaseInterface.getEntries()
        val listing = mutableListOf<Pair<Entry, Author>>()

        for (cur in entries) {
            listing.add(Pair(cur, databaseInterface.getAuthor(cur.authorId)))
        }

        return listing
    }

    override fun removeEntry(entry: Pair<Entry, Author>) {
        if (!::databaseManager.isInitialized)
            return

        databaseManager.databaseInterface().removeEntry(entry.first)
        ApiConnector.removeEntry(entry.first.id)
    }

    override fun updateEntry(entry: Pair<Entry, Author>) {
        databaseManager.databaseInterface().updateEntry(entry.first)
    }

    override fun addEntry(title: String, content: String, recipient: String, deadline: Date) {
        ApiConnector.addEntry(title, content, recipient, deadline)
    }

    override fun refreshEntries(callback: () -> Unit) {
        launch(UI) {
            launch(CommonPool) {
                val data = ApiConnector.synchronizeNews()
                for (cur in data) {
                    databaseManager.databaseInterface().insert(cur.first)
                    databaseManager.databaseInterface().insert(cur.second)
                }
            }.join()
            callback()
        }
    }

    fun setDatabaseManager(manager: DatabaseManager?) {
        manager ?: return
        databaseManager = manager
    }

}