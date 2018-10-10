package de.slg.leoapp.timetable.ui.intro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.core.preferences.PreferenceManager
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.intro.IntroFragment
import de.slg.leoapp.core.utility.dpToPx
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.timetable.R
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.db.DatabaseManager
import de.slg.leoapp.timetable.task.DownloadFileTask
import de.slg.leoapp.timetable.task.ParseTask
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class TimetableFragment : IntroFragment() {

    private var data: MutableList<Course> = mutableListOf()

    private lateinit var recyclerView: RecyclerView

    private var loading = true

    private var selected = 0

    private lateinit var grade: String

    override fun getNextButton() = R.id.next

    override fun getContentView() = R.layout.timetable_fragment_intro_timetable

    override fun getFragmentTag() = "leoapp_fragment_intro_timetable"

    override fun canContinue(): Boolean {
        Log.e(getFragmentTag(), "1")
        loading = true
        view!!.findViewById<View>(R.id.progressBar).visibility = View.VISIBLE

        launch(CommonPool) {
            Log.e(getFragmentTag(), "2")
            val ids = Array(selected) { i -> data[i].id!! }

            DatabaseManager.getInstance(activity!!).databaseInterface().choseCourses(ids)
            Log.e(getFragmentTag(), "3")

            activity?.runOnUiThread {
                view?.findViewById<View>(R.id.progressBar)?.visibility = View.GONE
            }
            loading = false
        }

        while (loading);

        Log.e(getFragmentTag(), "4")

        return true
    }

    override fun getErrorMessage() = "Bitte wähle deine Stunden aus."

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        PreferenceManager.read(context!!) {
            grade = getString(PreferenceManager.User.GRADE, "Q1")
        }

        downloadFile()
    }

    private fun initRecyclerView() {
        recyclerView = view!!.findViewById(R.id.recyclerView1)

        recyclerView.layoutManager = object : LinearLayoutManager(view!!.context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }

            override fun supportsPredictiveItemAnimations() = false
        }
        recyclerView.adapter = SubjectAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerDecoration())

        loadData()
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
                loadData()
            }
        })
        task.execute(
                activity!!.openFileInput("stundenplan"),
                DatabaseManager.getInstance(activity!!)
        )
    }

    private fun loadData() {
        loading = true
        view?.findViewById<View>(R.id.progressBar)?.visibility = View.VISIBLE

        launch(CommonPool) {
            data = mutableListOf(*DatabaseManager.getInstance(activity!!).databaseInterface().getCourses(grade))

            activity?.runOnUiThread {
                recyclerView.adapter?.notifyDataSetChanged()
                view?.findViewById<View>(R.id.progressBar)?.visibility = View.GONE
            }
            loading = false
        }
    }

    private fun removeIntersecting(id: Long) {
        launch(CommonPool) {
            val courses = DatabaseManager.getInstance(activity!!).databaseInterface().getIntersectingCourses(id, grade)

            for (course in courses) {
                for (i in selected until data.size) {
                    if (data[i].id!! == course.id!!) {
                        data.removeAt(i)
                        activity?.runOnUiThread {
                            recyclerView.adapter?.notifyItemRemoved(i)
                        }
                        break
                    }
                }
            }

            loading = false
        }
    }

    private fun addIntersecting(id: Long) {
        launch(CommonPool) {
            val courses = DatabaseManager.getInstance(activity!!).databaseInterface().getIntersectingCourses(id, "Q1")

            data.addAll(courses)

            activity?.runOnUiThread {
                recyclerView.adapter?.notifyItemRangeInserted(data.size - courses.size, courses.size)
            }

            loading = false
        }
    }

    inner class SubjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if (position < selected) R.layout.timetable_item_course_selected else R.layout.timetable_item_course
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(
                    viewType,
                    parent,
                    false
            )
            view.setOnClickListener {
                if (!loading) {
                    loading = true

                    val position = (parent as RecyclerView).getChildAdapterPosition(view)

                    when (viewType) {
                        R.layout.timetable_item_course -> {
                            val course = data.removeAt(position)

                            data.add(selected, course)

                            notifyItemMoved(position, selected)

                            selected++

                            removeIntersecting(course.id!!)
                        }
                        R.layout.timetable_item_course_selected -> {
                            val course = data.removeAt(position)

                            data.add(course)

                            notifyItemMoved(position, data.size - 1)

                            selected--

                            addIntersecting(course.id!!)
                        }
                    }
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
            if (holder.itemViewType == R.layout.timetable_item_course) {
                view.findViewById<TextView>(R.id.teacher).text = item.teacher
            }
        }

    }

    inner class DividerDecoration : RecyclerView.ItemDecoration() {

        private val viewHeight = 33f.dpToPx(context!!).toFloat()
        private val dividerHeight = 1f.dpToPx(context!!).toFloat()
        private val dividerPadding = 16f.dpToPx(context!!).toFloat()

        private val paint = Paint()

        init {
            paint.color = R.color.colorDivider.toColor(context!!)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val index = parent.getChildAdapterPosition(child)

                if (index == selected) {
                    val bottom = child.top - dividerPadding

                    c.drawRect(0f, bottom - dividerHeight, c.width.toFloat(), (bottom), paint)
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.getChildAdapterPosition(view) == selected) {
                outRect.top = viewHeight.toInt()
            }
        }

    }

}