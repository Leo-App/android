package de.slg.leoapp.news.ui.main

import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry

interface INewsPresenter {
    fun onFABPressed()
    fun onSettingsPressed()
    fun onDeletePressed()
    fun onDeleteConfirmed()
    fun onProfilePressed()
    fun onNavigationPressed()
    fun onEntryShown(entry: Pair<Entry, Author>)
}