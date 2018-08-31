package de.slg.leoapp.news.data.http

import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.news.data.http.json.ParsedAuthor
import de.slg.leoapp.news.data.http.json.ParsedEntry
import retrofit2.Call
import retrofit2.http.*

interface NewsAPIEndpoint {

    @GET("news/get")
    fun listEntries(@Header("Authentication") key: String): Call<List<ParsedEntry>>

    @GET("user/get/{id}")
    fun getAuthor(@Header("Authentication") key: String, @Path("id") id: Int): Call<ParsedAuthor>

    @POST("news/add")
    fun addEntry(@Header("Authentication") key: String, @Body entry: Any): Call<Any>

    @DELETE("entry/remove/{id}")
    fun removeEntry(@Header("Authentication") key: String, @Path("id") id: Int) : Call<Int>

}