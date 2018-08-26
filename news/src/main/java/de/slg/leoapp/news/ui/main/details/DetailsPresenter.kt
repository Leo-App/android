package de.slg.leoapp.news.ui.main.details

import android.text.format.DateFormat
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.news.R
import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import java.util.*

class DetailsPresenter : AbstractPresenter<IDetailsView, INewsDataManager>(), IDetailsPresenter {
    override fun onEditStarted() {
        TODO("not implemented")
    }

    override fun onEditFinished() {
        TODO("not implemented")
    }

    override fun onDateClicked() {
        TODO("not implemented")
    }

    override fun onDatePickerDateSelected(d: Date) {
        TODO("not implemented")
    }

    override fun setEntry(entry: Pair<Entry, Author>) {
        getMvpView().setContent(entry.first.description)
        getMvpView().setTitle(entry.first.title)
        getMvpView().setInfoLine("${entry.second.lastName} | ${getMvpView().getViewContext().getString(R.string.deadline_desc)}:")

        val c = Calendar.getInstance(Locale.GERMAN)
        c.timeInMillis = entry.first.deadline.time

        getMvpView().setDate(DateFormat.format("dd.MM.yyyy", c).toString())
    }

    override fun onBackPressed() {
        getMvpView().getCallingActivity().showListing()
    }
}