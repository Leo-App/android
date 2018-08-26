package de.slg.leoapp.news.data.http

import de.slg.leoapp.news.data.http.json.ParsedAuthor
import de.slg.leoapp.news.data.http.json.ParsedEntry
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsAPIEndpoint {

    @GET("news/get")
    fun listEntries(): Call<List<ParsedEntry>>

    @GET("user/get/{id}")
    fun getAuthor(@Path("id") id: Int): Call<ParsedAuthor>

}