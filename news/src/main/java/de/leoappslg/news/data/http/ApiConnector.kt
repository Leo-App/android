package de.leoappslg.news.data.http

import androidx.annotation.WorkerThread
import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry
import de.leoappslg.news.data.http.json.ParsedAuthor
import de.leoappslg.news.data.http.json.ParsedEntry
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.utility.URL_PHP_SCHOOL
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

    private fun getEntriesOrNull(): List<ParsedEntry>? {
        val response = api.listEntries().execute()
        if (!response.isSuccessful)
            return null

        return response.body()
    }

    private fun getAuthorOrNull(id: Int): ParsedAuthor? {
        val response = api.getAuthor(id).execute()

        if (!response.isSuccessful)
            return null

        return response.body()
    }

}