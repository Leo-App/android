package de.slg.leoapp.substitutions.ui

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.substitutions.R

class SubstitutionsActivity : LeoAppFeatureActivity() {
    override fun getContentView() = R.layout.substitutions_activity_main

    override fun getNavigationHighlightId() = R.string.substitutions_feature_title

    override fun getActivityTag() = "substitutions_feature_main"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        findViewById<TabLayout>(R.id.tabLayout).setupWithViewPager(findViewById(R.id.viewPager))
    }
}
