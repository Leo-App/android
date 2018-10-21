package de.slg.leoapp.exams.ui

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.dpToPx
import de.slg.leoapp.core.utility.spToPx
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.exams.R
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.data.db.Exam
import de.slg.leoapp.exams.parser.XMLParser
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : LeoAppFeatureActivity() {

    private var data: Array<Exam> = arrayOf()

    private lateinit var recyclerView: RecyclerView

    private var loading = false

    override fun getContentView() = R.layout.exams_activity_main

    override fun getNavigationHighlightId() = R.string.exams_feature_title

    override fun getActivityTag() = "feature_exams_main"

    override fun getActionIcon() = R.drawable.ic_add

    override fun getAction() = { _: View -> startActivity(Intent(applicationContext, DetailActivity::class.java)); }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        initRecyclerView()
        download()
    }

    override fun onResume() {
        super.onResume()

        if (!loading) {
            refreshData()
        }
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = ExamAdapter()
        recyclerView.addItemDecoration(MonthDecoration())
        recyclerView.addItemDecoration(DayDecoration())
    }

    private fun download() {
        loading = true

        runOnUiThread {
            findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
        }

        launch(CommonPool) {

            var lastModified = 0L

            if (fileList().contains("klausurplan")) {
                print(filesDir.absolutePath + "/klausurplan")
                val file = File(filesDir.absolutePath + "/klausurplan")
                lastModified = file.lastModified()
            }

            try {
                val connection: URLConnection = URL("https://ucloud4schools.de/ext/slg/leoapp_php/klausurplan/aktuell.xml")
                        .openConnection()
                connection.connectTimeout = 3000

                val date = connection.lastModified

                if (date == 0L || date > lastModified) {

                    val download = BufferedReader(
                            InputStreamReader(
                                    connection.getInputStream()
                            )
                    )

                    val writer = BufferedWriter(
                            OutputStreamWriter(
                                    applicationContext.openFileOutput("klausurplan", Context.MODE_PRIVATE)
                            )
                    )

                    for (s: String in download.readLines()) {
                        writer.write(s)
                        writer.newLine()
                    }

                    download.close()
                    writer.close()

                    parse()

                } else {
                    refreshData()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun parse() {
        loading = true

        runOnUiThread {
            findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
        }

        launch(CommonPool) {

            val parser = XMLParser(applicationContext.openFileInput("klausurplan"))
            val klausuren = parser.parse()

            val db = DatabaseManager.getInstance(applicationContext)

            db.clearAllTables()
            for (k in klausuren) {
                db.databaseInterface().insertExam(k)
            }

            refreshData()

        }
    }

    private fun refreshData() {
        loading = true

        runOnUiThread {
            findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
        }

        launch(CommonPool) {

            data = DatabaseManager.getInstance(applicationContext).databaseInterface().getExams()

            runOnUiThread {
                recyclerView.adapter!!.notifyDataSetChanged()
                findViewById<View>(R.id.progressBar).visibility = View.GONE
            }

            loading = false

        }
    }

    inner class ExamAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
                layoutInflater.inflate(
                        R.layout.exams_item_exam,
                        parent,
                        false
                )
        ) {}

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val klausur: TextView = holder.itemView.findViewById(R.id.klausur)
            val background: CardView = holder.itemView.findViewById(R.id.background)

            val k: Exam = data[position]

            klausur.text = k.fach.name
            background.setCardBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            k.fach.color
                    )
            )

            holder.itemView.setOnClickListener {
                startActivity(
                        Intent(applicationContext, DetailActivity::class.java)
                                .putExtra("id", k.id)
                )
            }
        }
    }

    inner class MonthDecoration : RecyclerView.ItemDecoration() {

        private val dividerHeight = 40f.dpToPx(applicationContext).toFloat()
        private val margin = 6f.dpToPx(applicationContext)

        private val paint = Paint()

        init {
            paint.textSize = 20f.spToPx(applicationContext)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val adapterPosition = parent.getChildAdapterPosition(child)

                if (hasHeader(adapterPosition)) {
                    val bottom = child.top - margin
                    val top = bottom - dividerHeight

                    val headerText = getHeaderText(adapterPosition)
                    val length = paint.measureText(headerText)

                    paint.color = R.color.colorTextLight.toColor(applicationContext)
                    c.drawText(headerText, 80f.dpToPx(applicationContext).toFloat(), (bottom - 14f.dpToPx(applicationContext)).toFloat(), paint)

                    paint.color = R.color.colorDivider.toColor(applicationContext)
                    c.drawRect(80f.dpToPx(applicationContext) + length + 8f.dpToPx(applicationContext), top + dividerHeight / 2 - 0.5f.dpToPx(applicationContext), c.width.toFloat(), top + dividerHeight / 2 + 0.5f.dpToPx(applicationContext), paint)
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val index = parent.getChildAdapterPosition(view)
            if (hasHeader(index)) {
                outRect.top = margin + dividerHeight.toInt() + margin
            } else {
                outRect.top = margin
            }
            outRect.bottom = margin
        }

        private fun hasHeader(index: Int): Boolean {
            if (index == 0) {
                return true
            } else {
                val c1 = GregorianCalendar()
                c1.time = data[index - 1].datum
                val c2 = GregorianCalendar()
                c2.time = data[index].datum
                if (c1[Calendar.YEAR] != c2[Calendar.YEAR] || c1[Calendar.MONTH] != c2[Calendar.MONTH]) {
                    return true
                }
            }
            return false
        }

        private fun getHeaderText(index: Int): String {
            val c1 = GregorianCalendar()
            val c2 = GregorianCalendar()
            c2.time = data[index].datum
            return SimpleDateFormat(
                    if (c1[Calendar.YEAR] == c2[Calendar.YEAR]) "MMMM"
                    else "MMMM yyyy",
                    Locale.GERMANY
            ).format(data[index].datum)
        }

    }

    inner class DayDecoration : RecyclerView.ItemDecoration() {

        private val left = 56f.dpToPx(applicationContext)
        private val right = 12f.dpToPx(applicationContext)

        private val paint = Paint()

        init {
            paint.textSize = 20f.spToPx(applicationContext)
            paint.color = R.color.colorTextLight.toColor(applicationContext)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val adapterPosition = parent.getChildAdapterPosition(child)

                if (hasHeader(adapterPosition)) {
                    val headerText = getHeaderText(adapterPosition)

                    c.drawText(headerText, 0, 2, 12f.dpToPx(applicationContext).toFloat(), (child.top + child.height / 2 - 3f.dpToPx(applicationContext)).toFloat(), paint)
                    c.drawText(headerText, 2, 4, 12f.dpToPx(applicationContext).toFloat(), (child.top + child.height / 2 - 3f.dpToPx(applicationContext)).toFloat() + paint.textSize, paint)
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = left
            outRect.right = right
        }

        private fun hasHeader(index: Int): Boolean {
            if (index == 0) {
                return true
            } else {
                val c1 = GregorianCalendar()
                c1.time = data[index - 1].datum
                val c2 = GregorianCalendar()
                c2.time = data[index].datum
                if (c1[Calendar.YEAR] != c2[Calendar.YEAR] || c1[Calendar.MONTH] != c2[Calendar.MONTH] || c1[Calendar.DAY_OF_MONTH] != c2[Calendar.DAY_OF_MONTH]) {
                    return true
                }
            }
            return false
        }

        private fun getHeaderText(index: Int): String {
            return SimpleDateFormat("ddEE", Locale.GERMANY).format(data[index].datum)
        }

    }

}