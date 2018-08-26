package de.leoappslg.news.ui.main

import de.leoappslg.news.data.db.Author
import de.leoappslg.news.data.db.Entry
import de.leoappslg.news.ui.main.add.IAddPresenter
import de.leoappslg.news.ui.main.details.IDetailsPresenter
import de.leoappslg.news.ui.main.listing.IListPresenter
import de.slg.leoapp.core.ui.mvp.MVPView

interface INewsView : MVPView {
    fun showFAB()
    fun hideFAB()
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
    fun showListing()
    fun openNewEntryDialog()
    fun showEntry(entry: Pair<Entry, Author>)
    fun openProfileActivity()
    fun openSettings()

    //Child views
    fun getDetailsPresenter() : IDetailsPresenter
    fun getListPresenter() : IListPresenter
    fun getAddPresenter() : IAddPresenter
}