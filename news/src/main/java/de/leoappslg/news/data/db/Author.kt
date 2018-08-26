package de.leoappslg.news.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.slg.leoapp.core.data.ProfilePicture

@Entity(tableName = "authors")
data class Author(@PrimaryKey val id: Int,
                  @ColumnInfo(name = "first_name") var firstName: String,
                  @ColumnInfo(name = "last_name") var lastName: String,
                  var profileImage: ProfilePicture)