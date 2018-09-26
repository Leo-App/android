package de.slg.leoapp.timetable

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.timetable.task.DownloadFileTask
import de.slg.leoapp.timetable.task.ParseTask

class MainActivity : LeoAppFeatureActivity() {
    private var downloadTask: DownloadFileTask? = null
    private var parseTask: ParseTask? = null

    override fun getContentView() = R.layout.timetable_activity_main

    override fun getNavigationHighlightId() = R.string.timetable_feature_title

    override fun getActivityTag() = "timetable_feature_main"

    @Suppress("UNUSED_VARIABLE")
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val monday: Array<LinearLayout> = arrayOf(
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

        val tuesday: Array<LinearLayout> = arrayOf(
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

        val wednesday: Array<LinearLayout> = arrayOf(
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

        val thursday: Array<LinearLayout> = arrayOf(
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

        val friday: Array<LinearLayout> = arrayOf(
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
    }

    private fun downloadFile() {
        if (downloadTask != null) {
            return
        }

        downloadTask = DownloadFileTask()
        downloadTask!!.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                parseFile()
                downloadTask = null
            }
        })
        downloadTask!!.execute(openFileOutput("stundenplan", Context.MODE_PRIVATE))
    }

    private fun parseFile() {
        if (parseTask != null) {
            return
        }

        parseTask = ParseTask()
        parseTask!!.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                refreshData()
                parseTask = null
            }
        })
    }

    private fun refreshData() {

    }
}