package de.leoappslg.news.ui.main.listing

import de.leoappslg.news.data.NewsDataManager
import de.leoappslg.news.ui.main.INewsView
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class ListPresenter : AbstractPresenter<IListView, NewsDataManager>(), IListPresenter {
    override fun onCardClick(index: Int) {
        TODO("not implemented")
    }
}