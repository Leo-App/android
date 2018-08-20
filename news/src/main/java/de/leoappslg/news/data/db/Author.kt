package de.leoappslg.news.data.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import de.slg.leoapp.core.data.ProfilePicture

@Entity(tableName = "authors")
data class Author(val id: Int,
                  @ColumnInfo(name = "first_name") val firstName: String,
                  @ColumnInfo(name = "first_name") val lastName: String,
                  val profileImage: ProfilePicture)