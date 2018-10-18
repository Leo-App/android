package de.slg.leoapp.news.data.http

import androidx.annotation.WorkerThread
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.utility.URL_PHP_SCHOOL
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.data.http.json.ParsedAuthor
import de.slg.leoapp.news.data.http.json.ParsedEntry
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

object ApiConnector {

    private val api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(URL_PHP_SCHOOL)
            .build()
            .create(NewsAPIEndpoint::class.java)

    @WorkerThread
    fun synchronizeNews(userId: Int, apiKey: String): List<Pair<Entry, Author>> {
        val list = mutableListOf<Pair<Entry, Author>>()

        val listing = getEntriesOrNull(userId, apiKey) ?: return list
        for (cur in listing) {
            val authorInfo = getAuthorOrNull(cur.author, apiKey) ?: continue

            list.add(
                    Pair(Entry(cur.id, cur.title, cur.content, cur.author, cur.counter, Date(cur.deadline)),
                    Author(authorInfo.id, authorInfo.firstName, authorInfo.lastName, ProfilePicture(authorInfo.pictureResource)))
            )
        }

        return list
    }

    fun removeEntry(id: Int, apiKey: String): Boolean {
        val request = api.removeEntry(apiKey, id).execute()
        return request.isSuccessful
    }

    fun addEntry(author: Int, title: String, content: String, recipient: String, deadline: Date, apiKey: String): Boolean {
        @Suppress("unused")
        val request = api.addEntry(apiKey, object {
            val title = title
            val author = author
            val content = content
            val recipient = recipient
            val deadline = deadline
        }).execute()

        return request.isSuccessful
    }

    fun addEntry(author: Int, title: String, content: String, recipients: List<Int>, deadline: Date, apiKey: String): Boolean {
        @Suppress("unused")
        val request = api.addEntry(apiKey, object {
            val title = title
            val author = author
            val content = content
            val recipients = recipients
            val deadline = deadline
        }).execute()

        return request.isSuccessful
    }

    fun updateEntry(id: Int, properties: Map<String, Any>, apiKey: String): Boolean {
        val request = api.updateEntry(apiKey, ("id" to id) + properties).execute()
        return request.isSuccessful
    }

    private fun getEntriesOrNull(userId: Int, apiKey: String): List<ParsedEntry>? {
        val response = api.listEntries(apiKey, userId).execute()
        if (!response.isSuccessful)
            return null

        return response.body()
    }

    private fun getAuthorOrNull(id: Int, apiKey: String): ParsedAuthor? {
        val response = api.getAuthor(apiKey, id).execute()

        if (!response.isSuccessful)
            return null

        return response.body()
    }

    private operator fun <X, Y> Pair<X, Y>.plus(map: Map<X, Y>): Map<X, Y> {
        return mutableMapOf(this) + map
    }

}