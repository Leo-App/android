package de.slg.leoapp.news.ui.main

import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.NewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.DatabaseManager
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.core.utility.PERMISSION_TEACHER
import de.slg.leoapp.core.utility.User
import de.slg.leoapp.news.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class NewsPresenter : AbstractPresenter<INewsView, INewsDataManager>(), INewsPresenter {

    private var state = State.LIST //we are keeping track of what's displayed in the top level presenter

    override fun onViewAttached(view: INewsView) {
        super.onViewAttached(view)
        registerDataManager(NewsDataManager)
        NewsDataManager.setDatabaseManager(DatabaseManager.getInstance(getMvpView().getViewContext()))

        if (User(getMvpView().getViewContext()).permission >= PERMISSION_TEACHER) getMvpView().showFAB()
        else getMvpView().hideFAB()

        //we synchronize once at view binding...
        getMvpView().showLoadingIndicator() //TODO check if that works (progress indicator gets hidden after listing sync)
        launch (UI) {
            getMvpView().showListing() //... and notify the views (transitive ListFragment)
            getMvpView().hideLoadingIndicator()
        }
    }

    override fun onBackPressed() {
        when (state) {
            NewsPresenter.State.DETAILS,
            NewsPresenter.State.ADD -> getMvpView().showListing()
            NewsPresenter.State.LIST -> getMvpView().terminate()
        }
    }

    override fun onFABPressed() {
        //todo
        when (state) {
            //Notify details presenter + view of FAB interaction
            NewsPresenter.State.DETAILS -> getMvpView().getDetailsPresenter().onEditStarted()
            NewsPresenter.State.ADD -> {
                //todo check if successful, add to local database, make api call etc
                getDataManager().getCurrentEntries()
                getMvpView().showListing()
                getMvpView().setFABIcon(R.drawable.ic_add)
                state = State.LIST //if adding was successful, return to listing
            }
            NewsPresenter.State.LIST -> {
                getMvpView().openNewEntryDialog()
                state = State.ADD
            }
        }
    }

    override fun onDeletePressed() {
        //todo prompt confirmation
    }

    override fun onDeleteConfirmed() {
        //todo notify other presenters, delete from databases after confirmation by user
    }

    override fun onProfilePressed() {
        getMvpView().openProfileActivity()
    }

    override fun onSettingsPressed() {
        getMvpView().openSettings()
    }

    override fun onNavigationPressed() {
        //todo
    }

    override fun onEntryShown(entry: Pair<Entry, Author>) {
        state = State.DETAILS

        if (User(getMvpView().getViewContext()).permission >= PERMISSION_TEACHER) {
            //todo show fab, show delete button, put settings and profile into overflow menu
        }

        getMvpView().getDetailsPresenter().setEntry(entry)
    }

    private enum class State { DETAILS, ADD, LIST }

}