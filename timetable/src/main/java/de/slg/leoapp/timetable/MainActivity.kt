package de.slg.leoapp.timetable

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.timetable.task.DownloadFileTask
import de.slg.leoapp.timetable.task.ParseTask
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource
import java.util.*

class MainActivity : LeoAppFeatureActivity() {
    private var downloadTask: DownloadFileTask? = null
    private var parseTask: ParseTask? = null

    override fun getContentView() = R.layout.timetable_activity_main

    override fun getNavigationHighlightId() = R.string.timetable_feature_title

    override fun getActivityTag() = "timetable_feature_main"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val calendar = GregorianCalendar()

        val current: TextView = findViewById(
                when (calendar[Calendar.DAY_OF_WEEK]) {
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