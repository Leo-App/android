package de.leoappslg.news.ui.main.details

import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry
import java.util.*

interface IDetailsPresenter {
    fun setEntry(entry: Pair<Entry, Author>)
    fun onEditStarted()
    fun onEditFinished()
    fun onBackPressed()
    fun onDateClicked()
    fun onDatePickerDateSelected(d: Date)
}