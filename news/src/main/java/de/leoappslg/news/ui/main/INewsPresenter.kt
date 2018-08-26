package de.leoappslg.news.ui.main

import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry

interface INewsPresenter {
    fun onFABPressed()
    fun onSettingsPressed()
    fun onDeletePressed()
    fun onDeleteConfirmed()
    fun onProfilePressed()
    fun onNavigationPressed()
    fun onEntryShown(entry: Pair<Entry, Author>)
}