package de.slg.leoapp.exams

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.exams.task.DownloadFileTask
import de.slg.leoapp.exams.task.ParseTask
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : LeoAppFeatureActivity() {

    private lateinit var data: Array<Klausur>

    private lateinit var listView: ListView
    private lateinit var action: FloatingActionButton

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

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        initListView()
    }

    override fun initToolbar() {
        super.initToolbar()

        action = findViewById(R.id.action_main)
        action.setImageResource(R.drawable.ic_add)
    }

    private fun initListView() {
        data = Array(0) { Klausur("", Date(), R.color.colorEnglisch) }

        listView = findViewById(R.id.listView)

        listView.adapter = object : ArrayAdapter<Klausur>(applicationContext, R.layout.list_item_klausur) {
            override fun getCount(): Int {
                return data.size
            }

            override fun getItem(position: Int): Klausur {
                return data[position]
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val v: View = convertView
                        ?: layoutInflater.inflate(R.layout.list_item_klausur, parent, false)

                val tag: TextView = v.findViewById(R.id.tag)
                val klausur: TextView = v.findViewById(R.id.klausur)
                val background: CardView = v.findViewById(R.id.background)

                val k: Klausur = getItem(position)

                tag.text = SimpleDateFormat("dd", Locale.GERMAN).format(k.datum)
                klausur.text = k.title
                background.setCardBackgroundColor(ContextCompat.getColor(applicationContext, k.color))

                return v
            }
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

        }
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

                //action.setimage
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
                (listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                parse = null
            }
        })
        parse!!.execute(applicationContext.openFileInput("klausurplan"))
    }
}