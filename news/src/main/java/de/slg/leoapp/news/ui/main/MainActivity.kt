package de.slg.leoapp.news.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.add.AddFragment
import de.slg.leoapp.news.ui.main.add.AddPresenter
import de.slg.leoapp.news.ui.main.details.DetailsFragment
import de.slg.leoapp.news.ui.main.details.DetailsPresenter
import de.slg.leoapp.news.ui.main.listing.ListFragment
import de.slg.leoapp.news.ui.main.listing.ListPresenter
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.news.R

class MainActivity : LeoAppFeatureActivity(), INewsView {

    //Presenter
    private lateinit var presenter: NewsPresenter

    //Fragment presenters
    private lateinit var detailsPresenter: DetailsPresenter
    private lateinit var listPresenter: ListPresenter
    private lateinit var addPresenter: AddPresenter

    override fun onCreate(b: Bundle?) {
        presenter = NewsPresenter()
        presenter.onViewAttached(this)

        super.onCreate(b)
    }

    override fun showFAB() {
        findViewById<View>(R.id.fab).visibility = View.VISIBLE
    }

    override fun hideFAB() {
        findViewById<View>(R.id.fab).visibility = View.GONE
    }

    override fun showLoadingIndicator() {
        findViewById<View>(R.id.progress_indicator).visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        findViewById<View>(R.id.progress_indicator).visibility = View.GONE
    }

    override fun showListing() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ListFragment(listPresenter), "listing").commit()
    }

    override fun openNewEntryDialog() { //TODO add animation
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddFragment(addPresenter), "add").commit()
    }

    override fun showEntry(entry: Pair<Entry, Author>) { //TODO add animations and info
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(detailsPresenter), "details").commit()
    }

    override fun openSettings() {
        startActivity(Intent(applicationContext!!, Utils.Activity.getSettingsReference()))
    }

    override fun openProfileActivity() {
        startActivity(Intent(applicationContext!!, Utils.Activity.getProfileReference()))
    }

    override fun getDetailsPresenter() = detailsPresenter

    override fun getListPresenter() = listPresenter

    override fun getAddPresenter() = addPresenter

    override fun getViewContext() = applicationContext!!

    override fun getContentView() = R.layout.activity_listing

    override fun getNavigationHighlightId() = 0xdefa12

    override fun getActivityTag() = "news_feature_main"

}