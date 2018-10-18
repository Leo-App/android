package de.slg.leoapp.news.ui.main.details

import android.text.format.DateFormat
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.news.R
import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.NewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import java.text.SimpleDateFormat
import java.util.*

class DetailsPresenter : AbstractPresenter<IDetailsView, INewsDataManager>(), IDetailsPresenter {

    private lateinit var currentEntry: Pair<Entry, Author>

    init {
        registerDataManager(NewsDataManager)
    }

    override fun onEditStarted() {
        getMvpView().enableTextViewEditing()
    }

    override fun onEditFinished(callback: (Boolean) -> Unit) {
        getMvpView().disableTextViewEditing()
        currentEntry.first.content = getMvpView().getEditedContent()
        currentEntry.first.deadline = getMvpView().getEditedDate()
        getDataManager().updateEntry(currentEntry, getMvpView().getViewContext()) {
            callback(true)
        }
    }

    override fun onEditCancelled() {
        getMvpView().disableTextViewEditing()
    }

    override fun onDateClicked() {
        val c = Calendar.getInstance()
        c.time = currentEntry.first.deadline
        getMvpView().openDatePicker(c)
    }

    override fun onDatePickerDateSelected(d: Date) {
        getMvpView().setDate(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(d))
    }

    override fun setEntry(entry: Pair<Entry, Author>) {
        currentEntry = entry

        getMvpView().setContent(entry.first.content)
        getMvpView().setTitle(entry.first.title)
        getMvpView().setInfoLine("${entry.second.lastName} | ${getMvpView().getViewContext().getString(R.string.news_deadline_desc)}:")

        val c = Calendar.getInstance(Locale.GERMAN)
        c.timeInMillis = entry.first.deadline.time

        getMvpView().setDate(DateFormat.format("dd.MM.yyyy", c).toString())
    }

    override fun onBackPressed() {
        getMvpView().getCallingActivity().showListing()
    }
}