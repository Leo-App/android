package de.slg.leoapp.news.ui.main.details

import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import java.util.*

interface IDetailsPresenter {
    fun setEntry(entry: Pair<Entry, Author>)
    fun onEditStarted()
    fun onEditFinished()
    fun onEditCancelled()
    fun onBackPressed()
    fun onDateClicked()
    fun onDatePickerDateSelected(d: Date)
}