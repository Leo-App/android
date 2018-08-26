package de.slg.leoapp.exams

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun getNavigationHighlightId(): Int {
        return 0
    }

    override fun getActivityTag(): String {
        return "exams_feature_main"
    }
}