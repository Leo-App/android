package de.slg.leoapp.ui.settings

import android.os.Bundle
import android.view.View
import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.ui.settings.overview.OverviewFragment

class SettingsActivity : LeoAppFeatureActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, OverviewFragment(), "overview").commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 1) {
            supportFragmentManager.beginTransaction().remove(
                    supportFragmentManager.findFragmentByTag("notification")
                            ?: supportFragmentManager.findFragmentByTag("about")!!).commit()
        } else {
            finish()
        }
    }

    override fun getContentView(): Int = R.layout.activity_settings

    override fun getNavigationHighlightId() = -1

    override fun getActivityTag() = "leoapp_feature_settings"

}