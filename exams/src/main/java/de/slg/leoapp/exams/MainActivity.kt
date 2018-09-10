package de.slg.leoapp.exams

import android.content.Context
import android.content.Intent
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

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun getNavigationHighlightId(): Int {
        return R.string.feature_title_exams
    }

    override fun getActivityTag(): String {
        return "feature_exams_main"
    }

    override fun usesActionButton() = true

    override fun getActionIcon() = R.drawable.ic_add

    override fun getAction() = View.OnClickListener { download() }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = KlausurAdapter()

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

    inner class KlausurAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
                layoutInflater.inflate(
                        R.layout.list_item_klausur,
                        parent,
                        false
                )
        ) {}

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val tag: TextView = holder.itemView.findViewById(R.id.tag)
            val klausur: TextView = holder.itemView.findViewById(R.id.klausur)
            val background: CardView = holder.itemView.findViewById(R.id.background)

            val k: Klausur = data[position]

            tag.text = SimpleDateFormat("dd EE", Locale.GERMAN).format(k.datum).substring(0, 5)
            klausur.text = k.subject.name
            background.setCardBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            k.subject.color
                    )
            )

            holder.itemView.setOnClickListener {
                startActivity(
                        Intent(applicationContext, ExamEditActivity::class.java)
                                .putExtra("id", k.id)
                )
            }
        }


    }
}