package de.slg.leoapp.exams.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import de.slg.leoapp.core.task.TaskStatusListener
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.setTint
import de.slg.leoapp.exams.Klausur
import de.slg.leoapp.exams.R
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.data.db.Subject
import de.slg.leoapp.exams.task.RefreshExamTask
import java.text.SimpleDateFormat
import java.util.*

class ExamEditActivity : LeoAppFeatureActivity() {
    private val newDate: Calendar = GregorianCalendar()
    private lateinit var dialogCalendar: AppCompatDialog
    private lateinit var dialogSubject: AppCompatDialog

    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.GERMANY)

    private var data = Klausur(null, Subject("Other", R.color.colorOther), Date())

    override fun getActivityTag() = "exams_feature_edit"

    override fun getContentView() = R.layout.exams_activity_detail

    override fun getActionIcon() = R.drawable.ic_check_black

    override fun getAction() = { _: View ->
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

    override fun getNavigationHighlightId() = R.string.exams_feature_title

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        dialogCalendar = AppCompatDialog(this)
        dialogCalendar.setContentView(R.layout.exams_dialog_date_picker)

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
        dialogSubject.setContentView(R.layout.exams_dialog_subject_picker)

        dialogSubject.findViewById<View>(R.id.item_other)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorOther)
        dialogSubject.findViewById<View>(R.id.item_other)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_other)
        dialogSubject.findViewById<View>(R.id.item_other)?.setOnClickListener {
            data = Klausur(data.id, Subject("Other", R.color.colorOther), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_math)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorMath)
        dialogSubject.findViewById<View>(R.id.item_math)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_math)
        dialogSubject.findViewById<View>(R.id.item_math)?.setOnClickListener {
            data = Klausur(data.id, Subject("Mathe", R.color.colorMath), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_german)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorGerman)
        dialogSubject.findViewById<View>(R.id.item_german)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_german)
        dialogSubject.findViewById<View>(R.id.item_german)?.setOnClickListener {
            data = Klausur(data.id, Subject("Deutsch", R.color.colorGerman), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_english)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorEnglish)
        dialogSubject.findViewById<View>(R.id.item_english)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_english)
        dialogSubject.findViewById<View>(R.id.item_english)?.setOnClickListener {
            data = Klausur(data.id, Subject("Englisch", R.color.colorEnglish), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_french)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorFrench)
        dialogSubject.findViewById<View>(R.id.item_french)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_french)
        dialogSubject.findViewById<View>(R.id.item_french)?.setOnClickListener {
            data = Klausur(data.id, Subject("Französisch", R.color.colorFrench), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_biology)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorBiology)
        dialogSubject.findViewById<View>(R.id.item_biology)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_biology)
        dialogSubject.findViewById<View>(R.id.item_biology)?.setOnClickListener {
            data = Klausur(data.id, Subject("Biologie", R.color.colorBiology), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_chemistry)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorChemistry)
        dialogSubject.findViewById<View>(R.id.item_chemistry)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_chemistry)
        dialogSubject.findViewById<View>(R.id.item_chemistry)?.setOnClickListener {
            data = Klausur(data.id, Subject("Chemie", R.color.colorChemistry), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_physics)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorPhysics)
        dialogSubject.findViewById<View>(R.id.item_physics)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_physics)
        dialogSubject.findViewById<View>(R.id.item_physics)?.setOnClickListener {
            data = Klausur(data.id, Subject("Physik", R.color.colorPhysics), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_CS)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorCS)
        dialogSubject.findViewById<View>(R.id.item_CS)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_cs)
        dialogSubject.findViewById<View>(R.id.item_CS)?.setOnClickListener {
            data = Klausur(data.id, Subject("Informatik", R.color.colorCS), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Politics)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorPolitics)
        dialogSubject.findViewById<View>(R.id.item_Politics)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_politics)
        dialogSubject.findViewById<View>(R.id.item_Politics)?.setOnClickListener {
            data = Klausur(data.id, Subject("Politik", R.color.colorPolitics), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Religion)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorReligion)
        dialogSubject.findViewById<View>(R.id.item_Religion)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_religion)
        dialogSubject.findViewById<View>(R.id.item_Religion)?.setOnClickListener {
            data = Klausur(data.id, Subject("Philosophie", R.color.colorReligion), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Spanish)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorSpanish)
        dialogSubject.findViewById<View>(R.id.item_Spanish)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_spanish)
        dialogSubject.findViewById<View>(R.id.item_Spanish)?.setOnClickListener {
            data = Klausur(data.id, Subject("Spanisch", R.color.colorSpanish), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Sport)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorSport)
        dialogSubject.findViewById<View>(R.id.item_Sport)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_sport)
        dialogSubject.findViewById<View>(R.id.item_Sport)?.setOnClickListener {
            data = Klausur(data.id, Subject("Sport", R.color.colorSport), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Dutch)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorDutch)
        dialogSubject.findViewById<View>(R.id.item_Dutch)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_dutch)
        dialogSubject.findViewById<View>(R.id.item_Dutch)?.setOnClickListener {
            data = Klausur(data.id, Subject("Niederländisch", R.color.colorDutch), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Latin)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorLatin)
        dialogSubject.findViewById<View>(R.id.item_Latin)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_latin)
        dialogSubject.findViewById<View>(R.id.item_Latin)?.setOnClickListener {
            data = Klausur(data.id, Subject("Latein", R.color.colorLatin), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Geography)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorGeography)
        dialogSubject.findViewById<View>(R.id.item_Geography)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_geography)
        dialogSubject.findViewById<View>(R.id.item_Geography)?.setOnClickListener {
            data = Klausur(data.id, Subject("Erdkunde", R.color.colorGeography), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_History)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorHistory)
        dialogSubject.findViewById<View>(R.id.item_History)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_history)
        dialogSubject.findViewById<View>(R.id.item_History)?.setOnClickListener {
            data = Klausur(data.id, Subject("Geschichte", R.color.colorHistory), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Education)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorEducation)
        dialogSubject.findViewById<View>(R.id.item_Education)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_education)
        dialogSubject.findViewById<View>(R.id.item_Education)?.setOnClickListener {
            data = Klausur(data.id, Subject("Pädagogik", R.color.colorEducation), data.datum)
            dialogSubject.dismiss()
            refreshSubject()
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

        findViewById<View>(R.id.datum).setOnTouchListener { _, _ ->
            findViewById<View>(R.id.cardMain).bringToFront()

            findViewById<View>(R.id.datum).requestFocus()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(findViewById<View>(R.id.datum).windowToken, 0)

            dialogCalendar.show()

            true
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

    private fun refreshSubject() {
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

    override fun onResume() {
        super.onResume()

        findViewById<View>(R.id.datum).clearFocus()
        findViewById<View>(R.id.notizen).clearFocus()
    }
}