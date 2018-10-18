package de.slg.leoapp.messenger.ui

import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.messenger.R

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView() = R.layout.messenger_activity_main

    override fun getNavigationHighlightId() = R.string.messenger_feature_name

    override fun getActivityTag() = "messenger_feature_main"
}