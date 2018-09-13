package de.slg.leoapp.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.ui.home.adapter.FeatureAdapter
import de.slg.leoapp.utils.Animation
import de.slg.leoapp.utils.Circular
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : LeoAppFeatureActivity(), HomeView, Animation by Circular {

    private lateinit var presenter: HomePresenter

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        initTransition(b)
        Log.wtf("leoapp", (home_layout is android.view.ViewGroup).toString())
        presenter = HomePresenter()
        presenter.onViewAttached(this)
    }

    override fun openFeatureActivity(activity: Class<out LeoAppFeatureActivity>) {
        startActivity(Intent(applicationContext, activity))
    }

    override fun showFeatureList() {
        navigationRecyclerView.adapter = FeatureAdapter(presenter)
        navigationRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun getContentView() = R.layout.activity_home

    override fun getActivityTag() = "leoapp_feature_home"

    override fun getNavigationHighlightId() = R.string.home

    override fun getViewContext() = applicationContext!!

    private fun initTransition(b: Bundle?) {
        if (b == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (intent.hasExtra("x") && intent.hasExtra("y")) {
                home_layout.visibility = View.INVISIBLE

                val revealX = intent.getIntExtra("x", 0)
                val revealY = intent.getIntExtra("y", 0)

                val viewTreeObserver = home_layout.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            revealActivity(revealX, revealY, home_layout)
                            home_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
                }
            }
        } else {
            home_layout.visibility = View.VISIBLE
        }
    }
}