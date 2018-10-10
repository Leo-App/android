package de.slg.leoapp.timetable.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.timetable.R
import de.slg.leoapp.timetable.data.UILesson
import de.slg.leoapp.timetable.data.db.DatabaseManager
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class MainActivity : LeoAppFeatureActivity() {

    private val data: Array<List<UILesson>> = arrayOf(listOf(), listOf(), listOf(), listOf(), listOf())
    private val views: Array<Array<View>> = arrayOf(arrayOf(), arrayOf(), arrayOf(), arrayOf(), arrayOf())

    private var latestHour = 0

    private var loading = false

    override fun getContentView() = R.layout.timetable_activity_main

    override fun getNavigationHighlightId() = R.string.timetable_feature_title

    override fun getActivityTag() = "timetable_feature_main"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        views[0] = arrayOf(
                findViewById(R.id.lesson_monday_01),
                findViewById(R.id.lesson_monday_02),
                findViewById(R.id.lesson_monday_03),
                findViewById(R.id.lesson_monday_04),
                findViewById(R.id.lesson_monday_05),
                findViewById(R.id.lesson_monday_06),
                findViewById(R.id.lesson_monday_07),
                findViewById(R.id.lesson_monday_08),
                findViewById(R.id.lesson_monday_09),
                findViewById(R.id.lesson_monday_10)
        )
        views[1] = arrayOf(
                findViewById(R.id.lesson_tuesday_01),
                findViewById(R.id.lesson_tuesday_02),
                findViewById(R.id.lesson_tuesday_03),
                findViewById(R.id.lesson_tuesday_04),
                findViewById(R.id.lesson_tuesday_05),
                findViewById(R.id.lesson_tuesday_06),
                findViewById(R.id.lesson_tuesday_07),
                findViewById(R.id.lesson_tuesday_08),
                findViewById(R.id.lesson_tuesday_09),
                findViewById(R.id.lesson_tuesday_10)
        )
        views[2] = arrayOf(
                findViewById(R.id.lesson_wednesday_01),
                findViewById(R.id.lesson_wednesday_02),
                findViewById(R.id.lesson_wednesday_03),
                findViewById(R.id.lesson_wednesday_04),
                findViewById(R.id.lesson_wednesday_05),
                findViewById(R.id.lesson_wednesday_06),
                findViewById(R.id.lesson_wednesday_07),
                findViewById(R.id.lesson_wednesday_08),
                findViewById(R.id.lesson_wednesday_09),
                findViewById(R.id.lesson_wednesday_10)
        )
        views[3] = arrayOf(
                findViewById(R.id.lesson_thursday_01),
                findViewById(R.id.lesson_thursday_02),
                findViewById(R.id.lesson_thursday_03),
                findViewById(R.id.lesson_thursday_04),
                findViewById(R.id.lesson_thursday_05),
                findViewById(R.id.lesson_thursday_06),
                findViewById(R.id.lesson_thursday_07),
                findViewById(R.id.lesson_thursday_08),
                findViewById(R.id.lesson_thursday_09),
                findViewById(R.id.lesson_thursday_10)
        )
        views[4] = arrayOf(
                findViewById(R.id.lesson_friday_01),
                findViewById(R.id.lesson_friday_02),
                findViewById(R.id.lesson_friday_03),
                findViewById(R.id.lesson_friday_04),
                findViewById(R.id.lesson_friday_05),
                findViewById(R.id.lesson_friday_06),
                findViewById(R.id.lesson_friday_07),
                findViewById(R.id.lesson_friday_08),
                findViewById(R.id.lesson_friday_09),
                findViewById(R.id.lesson_friday_10)
        )

        loadData()
    }

    private fun loadData() {
        loading = true
        //TODO show ProgressBar

        launch(CommonPool) {
            for (i in 0 until 5) {
                val list = DatabaseManager.getInstance(applicationContext).databaseInterface().getUILessons(i + 1)
                data[i] = list
                val last = list.last().hour
                if (last > latestHour) {
                    latestHour = last
                }
            }

            runOnUiThread {
                refreshUI()

                //TODO hide ProgressBar
                loading = false
            }
        }
    }

    private fun refreshUI() {
        for (i in 0 until views.size) {
            for (lesson in data[i]) {
                val view = views[i][lesson.hour - 1]
                view.findViewById<View>(R.id.color).setBackgroundColor(lesson.subject.color.toColor(applicationContext))
                view.findViewById<TextView>(R.id.title).text = "${lesson.subject.name}\n${lesson.room}\n${lesson.teacher}"
                view.visibility = View.VISIBLE
            }
        }


    }
}