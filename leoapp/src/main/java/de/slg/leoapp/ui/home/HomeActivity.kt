package de.slg.leoapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.ui.home.adapter.FeatureAdapter

class HomeActivity : LeoAppFeatureActivity(), HomeView {
    override fun usesActionButton() = false

    override fun getActionIcon() = 0

    private lateinit var presenter: HomePresenter

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        presenter = HomePresenter()
        presenter.onViewAttached(this)
    }

    override fun openFeatureActivity(activity: Class<out LeoAppFeatureActivity>) {
        startActivity(Intent(applicationContext, activity))
    }

    override fun showFeatureList() {
        Log.wtf("leoapp", presenter.getModuleCount().toString())
        navigationRecyclerView.adapter = FeatureAdapter(presenter)
        navigationRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun getContentView() = R.layout.activity_home

    override fun getActivityTag() = "leoapp_feature_home"

    override fun getNavigationHighlightId() = R.string.home

    override fun getViewContext() = applicationContext!!
}