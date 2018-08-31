package de.slg.leoapp.exams

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun getNavigationHighlightId(): Int {
        return R.string.feature_title_exams
    }

    override fun getActivityTag(): String {
        return "feature_exams_main"
    }
}