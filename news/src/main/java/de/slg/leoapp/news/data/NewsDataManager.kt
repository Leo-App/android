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

object NewsDataManager : INewsDataManager {

    private lateinit var databaseManager: DatabaseManager

    override fun getCurrentEntries(callback: (List<Pair<Entry, Author>>) -> Unit) {
        if (!::databaseManager.isInitialized) {
            callback(emptyList())
            return
        }

        launch(UI) {
            val entryListing = async(CommonPool) {
                val databaseInterface = databaseManager.databaseInterface()
                val entries = databaseInterface.getEntries()
                val listing = mutableListOf<Pair<Entry, Author>>()

                for (cur in entries) {
                    listing.add(Pair(cur, databaseInterface.getAuthor(cur.authorId)))
                }

                listing
            }.await()

            callback(entryListing)
        }
    }

    override fun removeEntry(entry: Pair<Entry, Author>, context: Context, callback: (Boolean) -> Unit) {
        if (!::databaseManager.isInitialized) {
            callback(false)
            return
        }

        launch(UI) {
            val db = launch (CommonPool) {
                databaseManager.databaseInterface().removeEntry(entry.first)
            }
            val api = async(CommonPool) {
                ApiConnector.removeEntry(entry.first.id, getApiKey(context))
            }

            db.join()
            callback(api.await())
        }


    }

    override fun updateEntry(entry: Pair<Entry, Author>, context: Context, callback: (Boolean) -> Unit) {
        launch(UI) {
            val db = launch(CommonPool) {
                databaseManager.databaseInterface().updateEntry(entry.first)
            }
            val api = async(CommonPool) {
                ApiConnector.updateEntry(entry.first.id,
                        mapOf("content" to entry.first.content, "deadline" to entry.first.deadline.time),
                        getApiKey(context))
            }

            db.join()
            callback(api.await())
        }
    }

    override fun addEntry(author: Int, title: String, content: String, recipient: String, deadline: Date, context: Context, callback: (Boolean) -> Unit) {
        launch(UI) {
            launch(CommonPool) {
                ApiConnector.addEntry(author, title, content, recipient, deadline, getApiKey(context))
            }.join()
            callback(true)
        }
    }

    override fun refreshEntries(userId: Int, context: Context, callback: () -> Unit) {
        launch(UI) {
            launch(CommonPool) {
                val data = ApiConnector.synchronizeNews(userId, getApiKey(context))
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

    private fun getApiKey(context: Context): String {
        return Utils.Network.getAPIKey(context)
    }

}