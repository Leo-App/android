package de.slg.leoapp.timetable

import android.os.Bundle
import android.view.View
import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView() = R.layout.timetable_activity_main

    override fun getNavigationHighlightId() = R.string.feature_title_timetable

    override fun getActivityTag() = "timetable_feature_main"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        findViewById<View>(R.id.layoutTimetable).bringToFront()


    }
}