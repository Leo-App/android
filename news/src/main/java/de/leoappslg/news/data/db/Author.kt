package de.leoappslg.news.data.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import de.slg.leoapp.core.data.ProfilePicture

@Entity(tableName = "authors")
data class Author(@PrimaryKey val id: Int,
                  @ColumnInfo(name = "first_name") var firstName: String,
                  @ColumnInfo(name = "last_name") var lastName: String,
                  var profileImage: ProfilePicture)