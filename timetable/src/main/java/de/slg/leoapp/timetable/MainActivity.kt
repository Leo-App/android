package de.slg.leoapp.timetable

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

/**
 * @author Moritz
 * Erstelldatum: 13.09.2018
 */
class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView() = R.layout.activity_main

    override fun getNavigationHighlightId() = R.string.feature_title_timetable

    override fun getActivityTag() = "timetable_feature_main"
}