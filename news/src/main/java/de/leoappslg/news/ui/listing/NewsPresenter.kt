package de.leoappslg.news.ui.listing

import de.leoappslg.news.data.INewsDataManager
import de.leoappslg.news.data.NewsDataManager
import de.leoappslg.news.data.db.DatabaseManager
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class NewsPresenter : AbstractPresenter<INewsView, INewsDataManager>(), INewsPresenter {

    override fun onViewAttached(view: INewsView) {
        super.onViewAttached(view)
        NewsDataManager.setDatabaseManager(DatabaseManager.getInstance(getMvpView().getViewContext()))
        getMvpView().showListing(getDataManager().getCurrentEntries())
    }

    override fun onCardClick(index: Int) {
        TODO("not implemented")
    }

    override fun onFABPressed() {
        TODO("not implemented")
    }

    override fun onSettingsPressed() {
        TODO("not implemented")
    }

    override fun onDeletePressed() {
        TODO("not implemented")
    }

    override fun onProfilePressed() {
        TODO("not implemented")
    }

    override fun onNavigationPressed() {
        TODO("not implemented")
    }

}