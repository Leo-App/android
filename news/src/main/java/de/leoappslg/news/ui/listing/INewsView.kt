package de.leoappslg.news.ui.listing

import android.content.Context
import de.leoappslg.news.data.db.Entry

interface INewsView {
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
    fun showListing(entries: List<Entry>)
    fun openNewEntryDialog()
    fun enableTextViewEditing()
    fun openDatePicker()
    fun showEntry()
    fun returnToListing()
    fun deleteEntry()
    fun openProfileActivity()
    fun openSettings()
    fun getViewContext(): Context
}