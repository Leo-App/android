package de.slg.leoapp.news.ui.main

import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.core.utility.PERMISSION_TEACHER
import de.slg.leoapp.core.utility.User
import de.slg.leoapp.news.R
import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.NewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.DatabaseManager
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.dialog.ConfirmationDialog

class NewsPresenter : AbstractPresenter<INewsView, INewsDataManager>(), INewsPresenter {

    private var state = State.LIST //we are keeping track of what's displayed in the top level presenter
    private lateinit var currentEntry: Pair<Entry, Author> //we also keep a reference to the currently/last opened entry

    override fun onViewAttached(view: INewsView) {
        super.onViewAttached(view)
        registerDataManager(NewsDataManager)
        NewsDataManager.setDatabaseManager(DatabaseManager.getInstance(getMvpView().getViewContext()))

        if (User(getMvpView().getViewContext()).permission >= PERMISSION_TEACHER) getMvpView().showFAB()
        else getMvpView().hideFAB()

        val context = getMvpView().getViewContext()

        //we synchronize once at view binding...
        getMvpView().showLoadingIndicator()
        getDataManager().refreshEntries(User(context).id, context) {
            getMvpView().showListing() //... and notify the views (transitive ListFragment)
            getMvpView().hideLoadingIndicator()
        }
    }

    override fun onBackPressed() {
        when (state) {
            NewsPresenter.State.DETAILS,
            NewsPresenter.State.ADD -> getMvpView().showListing()
            NewsPresenter.State.LIST -> getMvpView().terminate()
            NewsPresenter.State.EDIT -> getMvpView().getDetailsPresenter().onEditCancelled()
        }
    }

    override fun onFABPressed() {

        val context = getMvpView().getViewContext()

        when (state) {
            //Notify details presenter + view of FAB interaction
            NewsPresenter.State.DETAILS -> {
                getMvpView().getDetailsPresenter().onEditStarted()
                state = State.EDIT
            }
            NewsPresenter.State.EDIT -> {
                getMvpView().getDetailsPresenter().onEditFinished {
                    if (it) {
                        getDataManager().refreshEntries(User(getMvpView().getViewContext()).id, context) {
                            getMvpView().showListing() //if editing was successful, return to listing
                        }
                    } else {
                        //editing wasn't successful, show error
                    }
                }
            }
            NewsPresenter.State.ADD -> {
                getMvpView().getAddPresenter().onAddFinished {
                    if (it) {
                        getDataManager().refreshEntries(User(getMvpView().getViewContext()).id, context) {
                            getMvpView().showListing() //if editing was successful, return to listing
                        }
                    } else {
                        //editing wasn't successful, show error
                    }
                }
            }
            NewsPresenter.State.LIST -> {
                getMvpView().openNewEntryDialog()
            }
        }
    }

    override fun onDeletePressed() {
        val dialog = ConfirmationDialog(this, getMvpView().getViewContext())
        dialog.show()
    }

    override fun onDeleteConfirmed() {
        getMvpView().getListPresenter().onCardDeleted(currentEntry)
        getMvpView().showListing() //maybe do on delay/callback
    }

    override fun onEntryShown(entry: Pair<Entry, Author>) {
        currentEntry = entry
        state = State.DETAILS

        if (User(getMvpView().getViewContext()).permission >= PERMISSION_TEACHER) {
            getMvpView().showFAB()
            getMvpView().addDeleteAction()
        }

        getMvpView().getDetailsPresenter().setEntry(entry)
    }

    override fun onListingShown() {
        getMvpView().setFABIcon(R.drawable.ic_add)
        getMvpView().removeDeleteAction()
        state = State.LIST
    }

    override fun onNewEntryDialogShown() {
        state = State.ADD
    }

    private enum class State { DETAILS, ADD, LIST, EDIT }

}