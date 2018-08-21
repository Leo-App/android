package de.leoappslg.news.ui.listing

import android.content.Context
import android.os.Bundle
import de.leoappslg.news.data.db.Entry
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.news.R

class MainActivity : LeoAppFeatureActivity(), INewsView {

    private lateinit var presenter: NewsPresenter

    override fun onCreate(b: Bundle?) {
        presenter = NewsPresenter()
        presenter.onViewAttached(this)
        super.onCreate(b)
    }

    override fun showLoadingIndicator() {
        TODO("not implemented")
    }

    override fun hideLoadingIndicator() {
        TODO("not implemented")
    }

    override fun showListing(entries: List<Entry>) {
        TODO("not implemented")
    }

    override fun openProfileActivity() {
        TODO("not implemented")
    }

    override fun openNewEntryDialog() {
        TODO("not implemented")
    }

    override fun enableTextViewEditing() {
        TODO("not implemented")
    }

    override fun openDatePicker() {
        TODO("not implemented")
    }

    override fun showEntry() {
        TODO("not implemented")
    }

    override fun returnToListing() {
        TODO("not implemented")
    }

    override fun deleteEntry() {
        TODO("not implemented")
    }

    override fun openSettings() {
        TODO("not implemented")
    }

    override fun getViewContext() = applicationContext!!

    override fun getContentView() = R.layout.activity_listing

    override fun getNavigationHighlightId() = 0xdefa12

    override fun getActivityTag() = "news_feature_main"

}