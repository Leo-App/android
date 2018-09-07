package de.slg.leoapp.news.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.news.R
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.add.AddFragment
import de.slg.leoapp.news.ui.main.add.AddPresenter
import de.slg.leoapp.news.ui.main.details.DetailsFragment
import de.slg.leoapp.news.ui.main.details.DetailsPresenter
import de.slg.leoapp.news.ui.main.listing.ListFragment
import de.slg.leoapp.news.ui.main.listing.ListPresenter
import kotlinx.android.synthetic.main.activity_news.*

class MainActivity : LeoAppFeatureActivity(), INewsView {

    //Presenter
    private lateinit var presenter: NewsPresenter

    //Fragment presenters
    private lateinit var detailsPresenter: DetailsPresenter
    private lateinit var listPresenter: ListPresenter
    private lateinit var addPresenter: AddPresenter

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        presenter = NewsPresenter()

        detailsPresenter = DetailsPresenter()
        listPresenter = ListPresenter()
        addPresenter = AddPresenter()

        presenter.onViewAttached(this)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun terminate() {
        finish()
    }

    override fun showFAB() {
        fab.visibility = View.VISIBLE
    }

    override fun hideFAB() {
        fab.visibility = View.GONE
    }

    override fun setFABIcon(icon: Int) {
        fab.setImageDrawable(ContextCompat.getDrawable(applicationContext, icon))
    }

    override fun showLoadingIndicator() {
        progress_indicator.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progress_indicator.visibility = View.GONE
    }

    override fun showListing() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ListFragment(listPresenter), "listing").commit()
        presenter.onListingShown()
    }

    override fun openNewEntryDialog() { //TODO add animation
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddFragment(addPresenter), "add").commit()
        presenter.onNewEntryDialogShown()
    }

    override fun showEntry(entry: Pair<Entry, Author>) { //TODO add animations and info
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(detailsPresenter), "details").commit()
        presenter.onEntryShown(entry)
    }

    override fun addDeleteAction() {
        getAppBar().replaceMenu(R.menu.menu_actions_delete)
    }

    override fun removeDeleteAction() {
        getAppBar().replaceMenu(R.menu.app_toolbar_default)
    }

    override fun getDetailsPresenter() = detailsPresenter

    override fun getListPresenter() = listPresenter

    override fun getAddPresenter() = addPresenter

    override fun getViewContext() = applicationContext!!

    override fun getContentView() = R.layout.activity_news

    override fun getNavigationHighlightId() = 0xdefa12

    override fun getActivityTag() = "news_feature_main"

}