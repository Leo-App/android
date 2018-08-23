package de.leoappslg.news.ui.main

import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry
import de.slg.leoapp.core.ui.mvp.MVPView

interface INewsView : MVPView {
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
    fun showListing(entries: List<Pair<Entry, Author>>)
    fun openNewEntryDialog()
    fun showEntry(entry: Pair<Entry, Author>)
    fun returnToListing()
    fun deleteEntry()
    fun openProfileActivity()
    fun openSettings()
}