package de.slg.leoapp.news.ui.main

import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry

interface INewsPresenter {
    fun onFABPressed()
    fun onSettingsPressed()
    fun onProfilePressed()
    fun onNavigationPressed()
    fun onBackPressed()
    fun onDeletePressed()
    fun onDeleteConfirmed()

    //State callbacks
    fun onListingShown()
    fun onEntryShown(entry: Pair<Entry, Author>)
    fun onNewEntryDialogShown()
}