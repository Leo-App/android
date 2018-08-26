package de.slg.leoapp.news.data.http.json

import com.squareup.moshi.Json

class ParsedEntry(
        @Json(name = "id") val id: Int,
        @Json(name = "title") val title: String,
        @Json(name = "content") val content: String,
        @Json(name = "recipient") val recipient: String,
        @Json(name = "view_counter") val counter: Int,
        @Json(name = "attachment") val file: String,
        @Json(name = "valid_until") val deadline: Long,
        @Json(name = "author") val author: Int
)