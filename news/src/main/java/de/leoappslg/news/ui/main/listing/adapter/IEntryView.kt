package de.leoappslg.news.ui.main.listing.adapter

import androidx.annotation.StringRes
import de.slg.leoapp.core.data.ProfilePicture

interface IEntryView {
    fun setAuthor(author: String)
    fun setAuthor(@StringRes author: Int)
    fun setViewCounter(views: String)
    fun setProfilePicture(picture: ProfilePicture)
    fun setTitle(title: String)
    fun setContent(content: String)
}