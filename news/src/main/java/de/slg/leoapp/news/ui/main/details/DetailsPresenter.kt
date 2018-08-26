package de.slg.leoapp.news.ui.main.details

import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
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
        TODO("not implemented")
    }

    override fun onBackPressed() {
        TODO("not implemented")
    }
}