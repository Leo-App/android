package de.leoappslg.news.ui.main

import de.leoappslg.news.data.INewsDataManager
import de.leoappslg.news.data.NewsDataManager
import de.leoappslg.news.data.db.DatabaseManager
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class NewsPresenter : AbstractPresenter<INewsView, INewsDataManager>(), INewsPresenter {

    override fun onViewAttached(view: INewsView) {
        super.onViewAttached(view)
        registerDataManager(NewsDataManager)
        NewsDataManager.setDatabaseManager(DatabaseManager.getInstance(getMvpView().getViewContext()))

        getMvpView().showLoadingIndicator()
        launch (UI) {
            val entries = async (CommonPool) { getDataManager().getCurrentEntries() }.await()
            getMvpView().hideLoadingIndicator()
            getMvpView().showListing(entries)
        }
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