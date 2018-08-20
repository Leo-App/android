package de.leoappslg.news.ui.listing

import de.leoappslg.news.data.INewsDataManager
import de.leoappslg.news.data.NewsDataManager
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class NewsPresenter : AbstractPresenter<INewsView, INewsDataManager>(), INewsPresenter {

    init {
        registerDataManager(NewsDataManager)
    }

    override fun onViewAttached(view: INewsView) {
        super.onViewAttached(view)
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