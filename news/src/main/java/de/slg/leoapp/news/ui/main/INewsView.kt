package de.slg.leoapp.news.ui.main

import androidx.annotation.DrawableRes
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.add.IAddPresenter
import de.slg.leoapp.news.ui.main.details.IDetailsPresenter
import de.slg.leoapp.news.ui.main.listing.IListPresenter
import de.slg.leoapp.core.ui.mvp.MVPView

interface INewsView : MVPView {
    fun showFAB()
    fun hideFAB()
    fun setFABIcon(@DrawableRes icon: Int)
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
    fun showListing()
    fun openNewEntryDialog()
    fun showEntry(entry: Pair<Entry, Author>)
    fun addDeleteAction()
    fun removeDeleteAction()
    fun terminate()

    //Child views
    fun getDetailsPresenter() : IDetailsPresenter
    fun getListPresenter() : IListPresenter
    fun getAddPresenter() : IAddPresenter
}