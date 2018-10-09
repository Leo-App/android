package de.slg.leoapp.timetable.ui.intro

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.intro.IntroFragment
import de.slg.leoapp.timetable.R
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.db.DatabaseManager
import de.slg.leoapp.timetable.task.DownloadFileTask
import de.slg.leoapp.timetable.task.ParseTask
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class TimetableFragment : IntroFragment() {

    private var data: Array<Course> = arrayOf()
    private val selected: List<Course> = List()
    private val selectedIds: ArrayList<Long> = arrayListOf()

    private lateinit var recyclerSelected: RecyclerView
    private lateinit var recyclerSelectable: RecyclerView

    private var loading = true

    override fun getNextButton() = R.id.next

    override fun getContentView() = R.layout.timetable_fragment_intro_timetable

    override fun getFragmentTag() = "leoapp_fragment_intro_timetable"

    override fun canContinue(): Boolean {
        return true
    }

    override fun getErrorMessage() = "Bitte wähle deine Stunden aus."

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViews()

        downloadFile()
    }

    private fun initRecyclerViews() {
        recyclerSelected = view!!.findViewById(R.id.recyclerView1)
        recyclerSelectable = view!!.findViewById(R.id.recyclerView2)

        recyclerSelectable.layoutManager = LinearLayoutManager(view!!.context)
        recyclerSelectable.adapter = SubjectAdapter()
        //recyclerSelectable.itemAnimator = DefaultItemAnimator()

        recyclerSelected.layoutManager = LinearLayoutManager(view!!.context)
        recyclerSelected.adapter = SelectedSubjectAdapter()
        //recyclerSelected.itemAnimator = DefaultItemAnimator()

        reloadData()
    }

    private fun downloadFile() {
        val task = DownloadFileTask()
        task.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                parseFile()
            }
        })
        task.execute(
                activity!!.openFileOutput("stundenplan", Context.MODE_PRIVATE)
        )
    }

    private fun parseFile() {
        val task = ParseTask()
        task.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                reloadData()
            }
        })
        task.execute(
                activity!!.openFileInput("stundenplan"),
                DatabaseManager.getInstance(activity!!)
        )
    }

    private fun reloadData() {
        loading = true
        view!!.findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
        launch(CommonPool) {
            data = DatabaseManager.getInstance(activity!!).databaseInterface().getPossibleCourses(selectedIds, "Q1")

            activity!!.runOnUiThread {
                recyclerSelectable.adapter?.notifyDataSetChanged()
                view!!.findViewById<View>(R.id.progressBar).visibility = View.GONE
                loading = false
            }
        }
    }

    inner class SubjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.timetable_item_course,
                    parent,
                    false
            )
            view.setOnClickListener {
                if (!loading) {
                    val item = data[(parent as RecyclerView).getChildAdapterPosition(view)]

                    selected.append(item)
                    selectedIds.add(item.id!!)

                    recyclerSelected.adapter!!.notifyDataSetChanged()

                    reloadData()
                }
            }

            return object : RecyclerView.ViewHolder(
                    view
            ) {}
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = data[position]

            val view = holder.itemView

            view.findViewById<TextView>(R.id.course).text = "${item.title} ${item.subject.name} ${item.type}"
            view.findViewById<TextView>(R.id.teacher).text = item.teacher
        }

    }

    inner class SelectedSubjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.timetable_item_course_selected,
                    parent,
                    false
            )
            view.setOnClickListener {
                if (loading) {
                    val position = (parent as RecyclerView).getChildAdapterPosition(view)

                    val id = selected.getObjectAt(position).id!!
                    selected.remove()
                    selectedIds.remove(id)

                    notifyDataSetChanged()

                    reloadData()
                }
            }

            return object : RecyclerView.ViewHolder(
                    view
            ) {}
        }

        override fun getItemCount() = selected.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = selected.getObjectAt(position)

            val view = holder.itemView

            view.findViewById<TextView>(R.id.course).text = item.title
        }

    }
}