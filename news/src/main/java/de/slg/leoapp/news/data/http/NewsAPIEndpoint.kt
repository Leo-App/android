package de.slg.leoapp.news.data.http

import de.slg.leoapp.news.data.http.json.ParsedAuthor
import de.slg.leoapp.news.data.http.json.ParsedEntry
import retrofit2.Call
import retrofit2.http.*

interface NewsAPIEndpoint {

    @GET("user/{id}/news")
    fun listEntries(@Header("Authentication") key: String, @Path("id") userId: Int): Call<List<ParsedEntry>>

    @GET("user/{id}")
    fun getAuthor(@Header("Authentication") key: String, @Path("id") id: Int): Call<ParsedAuthor>

    @POST("news/")
    fun addEntry(@Header("Authentication") key: String, @Body entry: Any): Call<Any>

    @POST("news/")
    fun updateEntry(@Header("Authentication") key: String, @Body entry: Any): Call<Any>

    @DELETE("entry/remove/{id}")
    fun removeEntry(@Header("Authentication") key: String, @Path("id") id: Int) : Call<Int>

}