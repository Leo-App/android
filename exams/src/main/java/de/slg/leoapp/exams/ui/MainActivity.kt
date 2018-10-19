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
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.dpToPx
import de.slg.leoapp.core.utility.spToPx
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.exams.Klausur
import de.slg.leoapp.exams.R
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.task.DownloadFileTask
import de.slg.leoapp.exams.task.ParseTask
import de.slg.leoapp.exams.task.RefreshDataTask
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : LeoAppFeatureActivity() {

    private var data: Array<Klausur> = arrayOf()

    private lateinit var recyclerView: RecyclerView

    private var download: DownloadFileTask? = null
    private var parse: ParseTask? = null

    override fun getContentView() = R.layout.exams_activity_main

    override fun getNavigationHighlightId() = R.string.exams_feature_title

    override fun getActivityTag() = "feature_exams_main"

    override fun getActionIcon() = R.drawable.ic_add

    override fun getAction() = { _: View -> download() }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = ExamAdapter()
        recyclerView.addItemDecoration(MonthDecoration())
        recyclerView.addItemDecoration(DayDecoration())

        refreshData()
    }

    private fun download() {
        if (download != null) {
            return
        }

        download = DownloadFileTask()
        download!!.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                parse()
                download = null
            }
        })
        download!!.execute(applicationContext.openFileOutput("klausurplan", Context.MODE_PRIVATE))
    }

    private fun parse() {
        if (parse != null) {
            return
        }

        parse = ParseTask()
        parse!!.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                refreshData()
                parse = null
            }
        })
        parse!!.execute(applicationContext.openFileInput("klausurplan"), DatabaseManager.getInstance(applicationContext))
    }

    private fun refreshData() {
        val task = RefreshDataTask()
        task.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                @Suppress("UNCHECKED_CAST")
                data = params[0] as Array<Klausur>
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        })
        task.execute(DatabaseManager.getInstance(applicationContext))
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

            val k: Klausur = data[position]

            klausur.text = k.subject.name
            background.setCardBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            k.subject.color
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
        private val padding = 6f.dpToPx(applicationContext)

        private val paint = Paint()

        init {
            paint.textSize = 20f.spToPx(applicationContext)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val adapterPosition = parent.getChildAdapterPosition(child)

                if (hasHeader(adapterPosition)) {
                    val top = child.top - dividerHeight

                    val headerText = getHeaderText(adapterPosition)
                    val length = paint.measureText(headerText)

                    paint.color = R.color.colorTextLight.toColor(applicationContext)
                    c.drawText(headerText, 80f.dpToPx(applicationContext).toFloat(), (child.top - 14f.dpToPx(applicationContext)).toFloat(), paint)

                    paint.color = R.color.colorDivider.toColor(applicationContext)
                    c.drawRect(80f.dpToPx(applicationContext) + length + 8f.dpToPx(applicationContext), top + dividerHeight / 2 - 0.5f.dpToPx(applicationContext), c.width.toFloat(), top + dividerHeight / 2 + 0.5f.dpToPx(applicationContext), paint)
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val index = parent.getChildAdapterPosition(view)
            if (hasHeader(index)) {
                outRect.top = dividerHeight.toInt()
            } else {
                outRect.top = padding
            }
            outRect.bottom = padding
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