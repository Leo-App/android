package de.slg.leoapp.timetable.ui.intro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.core.preferences.PreferenceManager
import de.slg.leoapp.core.ui.intro.IntroFragment
import de.slg.leoapp.core.utility.dpToPx
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.timetable.R
import de.slg.leoapp.timetable.data.Course
import de.slg.leoapp.timetable.data.Lesson
import de.slg.leoapp.timetable.data.db.Converters
import de.slg.leoapp.timetable.data.db.DatabaseManager
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.*
import java.net.URL
import java.net.URLConnection

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
        return true
    }

    override fun complete() {
        loading = true
        view!!.findViewById<View>(R.id.progressBar).visibility = View.VISIBLE

        launch(CommonPool) {
            val ids = Array(selected) { i -> data[i].id!! }

            DatabaseManager.getInstance(activity!!).databaseInterface().choseCourses(ids)

            activity?.runOnUiThread {
                view?.findViewById<View>(R.id.progressBar)?.visibility = View.GONE
            }
            loading = false
        }

        while (loading);

        super.complete()
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

        if (!loading) {
            loadData()
        }
    }

    private fun downloadFile() {
        loading = true
        activity?.runOnUiThread {
            view?.findViewById<View>(R.id.progressBar)?.visibility = View.VISIBLE
        }

        var lastModified = 0L

        if (activity!!.fileList().contains("stundenplan")) {
            print(activity!!.filesDir.absolutePath + "/stundenplan")
            val file = File(activity!!.filesDir.absolutePath + "/stundenplan")
            lastModified = file.lastModified()
        }

        launch(CommonPool) {

            try {

                val connection: URLConnection = URL("https://ucloud4schools.de/ext/slg/leoapp_php/stundenplan/aktuell.txt")
                        .openConnection()
                connection.connectTimeout = 3000

                val date = connection.lastModified

                if (date == 0L || date > lastModified) {

                    val download = BufferedReader(
                            InputStreamReader(
                                    connection.getInputStream(),
                                    "ISO-8859-1"
                            )
                    )

                    val writer = BufferedWriter(
                            OutputStreamWriter(
                                    activity!!.openFileOutput("stundenplan", Context.MODE_PRIVATE)
                            )
                    )

                    for (s: String in download.readLines()) {
                        writer.write(s)
                        writer.newLine()
                    }

                    download.close()
                    writer.close()

                    parseFile()

                } else {
                    loadData()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun parseFile() {
        launch(CommonPool) {

            var lastCourseName = ""
            var lastId: Long = 0

            val db = DatabaseManager.getInstance(activity!!)

            db.clearAllTables()

            val lines = BufferedReader(
                    InputStreamReader(
                            activity!!.openFileInput("stundenplan")
                    )
            ).readLines()

            val converter = Converters()

            for (line in lines) {

                val parts = line.substring(line.indexOf(',') + 1).replace("\"", "").split(",")
                val grade = parts[0]
                val teacher = parts[1]
                val courseName = parts[2].replace("-", " G")
                val room = parts[3]
                val day = parts[4]
                val hour = parts[5]

                if (grade.isBlank() || teacher.isBlank() || courseName.isBlank() || room.isBlank() || day.isBlank() || hour.isBlank())
                    continue
                if (courseName == "Vertr")
                    continue

                if (lastCourseName != courseName) {
                    val subject = converter.toSubject(when {
                        grade.matches(Regex("\\d\\d[abcf]")) -> courseName.toUpperCase().replace(Regex("\\d"), "")
                        courseName.startsWith("IB") -> courseName.toUpperCase().replace(Regex("\\d"), "")
                        courseName.contains('-') -> courseName.substring(0, courseName.indexOf('-')).toUpperCase().replace(Regex("\\d"), "")
                        courseName.contains(' ') -> courseName.substring(0, courseName.indexOf(' ')).toUpperCase().replace(Regex("\\d"), "")
                        courseName.contains(Regex("G\\d")) -> courseName.substring(0, courseName.lastIndexOf("G")).toUpperCase().replace(Regex("\\d"), "")
                        courseName.contains(Regex("L\\d")) -> courseName.substring(0, courseName.lastIndexOf("L")).toUpperCase().replace(Regex("\\d"), "")
                        else -> ""
                    })
                    val type = when {
                        courseName.matches(Regex("..L\\d")) -> "LK"
                        courseName.startsWith("AG") -> "AG"
                        else -> "GK"
                    }
                    val number = when {
                        courseName.last() in '1'..'9' -> courseName.last() - '0'
                        else -> 0
                    }

                    lastId = db.databaseInterface().insertCourse(Course(null, subject, number, type, teacher, grade, false, false))
                }

                db.databaseInterface().insertLesson(Lesson(lastId, Integer.parseInt(day), Integer.parseInt(hour), room))

                lastCourseName = courseName

            }

            loadData()

        }
    }

    private fun loadData() {
        loading = true
        activity?.runOnUiThread {
            view?.findViewById<View>(R.id.progressBar)?.visibility = View.VISIBLE
        }

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

            val title = if (item.number != 0) {
                "${item.subject.name} ${item.type} ${item.number}"
            } else {
                "${item.subject.name} ${item.type}"
            }

            view.findViewById<TextView>(R.id.title).text = title
            view.findViewById<TextView>(R.id.teacher).text = item.teacher
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