package de.slg.leoapp.ui.settings

import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class SettingsActivity : LeoAppFeatureActivity() {
    override fun getContentView(): Int = R.layout.activity_settings

    override fun getNavigationHighlightId(): Int {
        return -1
    }

    override fun getActivityTag(): String {
        return "leoapp_feature_settings"
    }
}