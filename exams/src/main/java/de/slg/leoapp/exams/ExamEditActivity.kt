package de.slg.leoapp.exams

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.data.db.Subject
import de.slg.leoapp.exams.task.RefreshExamTask
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 09.09.2018
 */
class ExamEditActivity : LeoAppFeatureActivity() {
    private val newDate: Calendar = GregorianCalendar()
    private lateinit var dialogCalendar: AppCompatDialog
    private lateinit var dialogSubject: AppCompatDialog

    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.GERMANY)

    private var data = Klausur(null, Subject("Other", R.color.colorOther), Date())

    override fun getActivityTag() = "exams_feature_edit"

    override fun getContentView() = R.layout.activity_klausur

    override fun usesActionButton() = true

    override fun getActionIcon() = R.drawable.ic_check

    override fun getAction() = View.OnClickListener {
        save()
        finish()
    }

    private fun save() {

    }

    private fun refreshData() {
        val task = RefreshExamTask()
        task.addListener(object : TaskStatusListener {
            override fun taskStarts() {

            }

            override fun taskFinished(vararg params: Any) {
                @Suppress("UNCHECKED_CAST")
                data = params[0] as Klausur
                findViewById<TextView>(R.id.datum).text = dateFormat.format(data.datum)
                findViewById<TextView>(R.id.chooseSubject).text = data.subject.name
                findViewById<CardView>(R.id.cardHeader).setCardBackgroundColor(
                        ContextCompat.getColor(
                                applicationContext,
                                data.subject.color
                        )
                )
                findViewById<View>(R.id.frameLayout).setBackgroundColor(
                        ContextCompat.getColor(
                                applicationContext,
                                data.subject.color
                        )
                )
            }
        })
        task.execute(
                DatabaseManager.getInstance(applicationContext),
                intent.getIntExtra("id", 0)
        )
    }

    override fun getNavigationHighlightId() = R.string.feature_title_exams

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        dialogCalendar = AppCompatDialog(this)
        dialogCalendar.setContentView(R.layout.dialog_date_picker)

        dialogCalendar.findViewById<CalendarView>(R.id.calendar)!!.setOnDateChangeListener { _, year, month, dayOfMonth ->
            newDate[Calendar.YEAR] = year
            newDate[Calendar.MONTH] = month
            newDate[Calendar.DAY_OF_MONTH] = dayOfMonth
        }

        dialogCalendar.findViewById<View>(R.id.buttonOk)!!.setOnClickListener {
            findViewById<TextView>(R.id.datum).text = dateFormat.format(newDate.time)

            dialogCalendar.dismiss()
        }

        dialogCalendar.findViewById<View>(R.id.buttonCancel)!!.setOnClickListener {
            dialogCalendar.dismiss()
        }

        dialogSubject = AppCompatDialog(this)
        dialogSubject.setContentView(R.layout.dialog_subject_picker)

        dialogSubject.findViewById<View>(R.id.buttonOk)!!.setOnClickListener {
            dialogSubject.dismiss()
        }

        dialogSubject.findViewById<View>(R.id.buttonCancel)!!.setOnClickListener {
            dialogSubject.dismiss()
        }

        findViewById<View>(R.id.imageButton).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.cardHeader).setOnClickListener {
            findViewById<View>(R.id.cardHeader).bringToFront()
        }

        findViewById<View>(R.id.cardMain).setOnClickListener {
            findViewById<View>(R.id.cardMain).bringToFront()
        }

        findViewById<View>(R.id.datum).setOnClickListener {
            findViewById<View>(R.id.cardMain).bringToFront()

            findViewById<View>(R.id.datum).requestFocus()

            dialogCalendar.show()
        }

        findViewById<View>(R.id.notizen).setOnClickListener {
            findViewById<View>(R.id.cardMain).bringToFront()
        }

        findViewById<View>(R.id.layout).setOnClickListener {
            findViewById<View>(R.id.cardHeader).bringToFront()

            dialogSubject.show()
        }

        refreshData()
    }

    override fun onResume() {
        super.onResume()

        findViewById<View>(R.id.datum).clearFocus()
        findViewById<View>(R.id.notizen).clearFocus()
    }
}