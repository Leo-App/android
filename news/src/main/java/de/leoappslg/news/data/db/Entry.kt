package de.leoappslg.news.data.db

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import java.util.*

@Entity(tableName = "entries", foreignKeys = [ForeignKey(
        entity = Author::class,
        parentColumns = ["id"],
        childColumns = ["authorId"],
        onDelete = CASCADE
)])
data class Entry(@PrimaryKey var id: Int,
                 var title: String,
                 var description: String,
                 @Ignore var author: Author,
                 val authorId: Author,
                 var views: Int,
                 var deadline: Date)