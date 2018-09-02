package de.slg.leoapp.ui.settings

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class SettingsActivity : LeoAppFeatureActivity() {
    override fun getContentView(): Int {
        TODO("not implemented")
    }

    override fun getNavigationHighlightId(): Int {
        return -1
    }

    override fun getActivityTag(): String {
        return "leoapp_feature_settings"
    }
}