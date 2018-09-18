package de.slg.leoapp.timetable

import android.os.Bundle
import android.view.View
import android.widget.TextView
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource
import java.util.*

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView() = R.layout.timetable_activity_main

    override fun getNavigationHighlightId() = R.string.timetable_feature_title

    override fun getActivityTag() = "timetable_feature_main"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        findViewById<View>(R.id.layoutTimetable).bringToFront()

        val calendar = GregorianCalendar()

        val current: TextView = findViewById(
                when(calendar[Calendar.DAY_OF_WEEK]) {
                    Calendar.TUESDAY -> R.id.titleTuesday
                    Calendar.WEDNESDAY -> R.id.titleWednesday
                    Calendar.THURSDAY -> R.id.titleThursday
                    Calendar.FRIDAY -> R.id.titleFriday
                    else -> R.id.titleMonday
                }
        )

        current.backgroundResource = R.drawable.dot_primary
        current.textColorResource = android.R.color.white
    }
}