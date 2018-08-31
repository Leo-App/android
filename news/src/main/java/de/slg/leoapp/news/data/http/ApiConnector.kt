package de.slg.leoapp.news.data.http

import android.content.Context
import androidx.annotation.WorkerThread
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.utility.URL_PHP_SCHOOL
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.data.http.json.ParsedAuthor
import de.slg.leoapp.news.data.http.json.ParsedEntry
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

object ApiConnector {

    lateinit var apiKey: String

    private val api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(URL_PHP_SCHOOL)
            .build()
            .create(NewsAPIEndpoint::class.java)

    @WorkerThread
    fun synchronizeNews(): List<Pair<Entry, Author>> {
        val list = mutableListOf<Pair<Entry, Author>>()

        val listing = getEntriesOrNull() ?: return list
        for (cur in listing) {
            val authorInfo = getAuthorOrNull(cur.author) ?: continue

            list.add(
                    Pair(Entry(cur.id, cur.title, cur.content, cur.author, cur.counter, Date(cur.deadline)),
                    Author(authorInfo.id, authorInfo.firstName, authorInfo.lastName, ProfilePicture(authorInfo.pictureResource)))
            )
        }

        return list
    }

    fun removeEntry(id: Int) {
        checkAPIKey()
        api.removeEntry(apiKey, id)
    }

    fun addEntry(title: String, content: String, recipient: String, deadline: Date) {
        checkAPIKey()
        api.addEntry(apiKey, object {
            val title = title
            val content = content
            val recipient = recipient
            val deadline = deadline
        })
    }

    private fun getEntriesOrNull(): List<ParsedEntry>? {
        checkAPIKey()
        val response = api.listEntries(apiKey).execute()
        if (!response.isSuccessful)
            return null

        return response.body()
    }

    private fun getAuthorOrNull(id: Int): ParsedAuthor? {
        checkAPIKey()
        val response = api.getAuthor(apiKey, id).execute()

        if (!response.isSuccessful)
            return null

        return response.body()
    }

    private fun checkAPIKey() {
        class APIKeyNotSetException(desc: String) : RuntimeException(desc)
        if (!::apiKey.isInitialized)
            throw APIKeyNotSetException("You need to initialize the API Key before making requests")
    }

}