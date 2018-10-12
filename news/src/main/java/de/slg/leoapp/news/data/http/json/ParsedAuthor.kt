package de.slg.leoapp.news.data.http.json

import com.squareup.moshi.Json

class ParsedAuthor(
        @Json(name = "id") val id: Int,
        @Json(name = "first_name") val firstName: String,
        @Json(name = "last_name") val lastName: String,
        @Json(name = "profile_picture") val pictureResource: String)