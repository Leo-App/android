package de.leoappslg.news.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "entries", foreignKeys = [ForeignKey(
        entity = Author::class,
        parentColumns = ["id"],
        childColumns = ["authorId"],
        onDelete = CASCADE
)], indices = [Index("authorId")])
data class Entry(@PrimaryKey var id: Int,
                 var title: String,
                 var description: String,
                 var authorId: Int,
                 var views: Int,
                 var deadline: Date)