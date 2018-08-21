package de.leoappslg.news.data.http

import androidx.annotation.WorkerThread
import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry
import de.slg.leoapp.core.utility.URL_PHP_SCHOOL
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

object ApiConnector {

    private const val url = URL_PHP_SCHOOL

    fun synchronizeNews(): List<Pair<Entry, Author>> {
        val list = mutableListOf<Pair<Entry, Author>>()
        val job = Job()

        launch (UI + job) {
            val json = async (CommonPool) { getJsonResponse("news/get") }.await()

            if (!json.getBoolean("success"))
                return@launch

            val entries = json.getJSONArray("data")

            for (i in 0 until entries.length()) {
                val cur = entries.getJSONObject(i)
            }

        }

        return list
    }

    @WorkerThread
    private fun getJsonResponse(path: String): JSONObject {
        return khttp.get("$url/$path").jsonObject
    }

}