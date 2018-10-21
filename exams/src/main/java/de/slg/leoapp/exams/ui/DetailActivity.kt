package de.slg.leoapp.exams.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.setTint
import de.slg.leoapp.core.utility.toColor
import de.slg.leoapp.exams.R
import de.slg.leoapp.exams.data.db.Converters
import de.slg.leoapp.exams.data.db.DatabaseManager
import de.slg.leoapp.exams.data.db.Exam
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.backgroundDrawable
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : LeoAppFeatureActivity() {

    private val newDate: Calendar = GregorianCalendar()

    private val converters = Converters()

    private lateinit var header: View
    private lateinit var main: View

    private lateinit var dialogDate: AppCompatDialog
    private lateinit var dialogSubject: AppCompatDialog

    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.GERMANY)

    private var id = 0

    private var data = Exam(null, Date(), converters.toSubject(""), null, null, null)

    override fun getActivityTag() = "exams_feature_edit"

    override fun getContentView() = R.layout.exams_activity_detail

    override fun getNavigationHighlightId() = R.string.exams_feature_title

    override fun getActionIcon() = R.drawable.ic_check_black

    override fun getAction() = { _: View ->
        save()
        finish()
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        header = findViewById(R.id.header)
        main = findViewById(R.id.cardMain)

        findViewById<View>(R.id.imageButton).setOnClickListener {
            finish()
        }

        header.setOnClickListener {
            header.bringToFront()
        }

        main.setOnClickListener {
            main.bringToFront()
        }

        findViewById<View>(R.id.datum).setOnTouchListener { _, _ ->
            main.bringToFront()

            findViewById<View>(R.id.datum).requestFocus()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(findViewById<View>(R.id.datum).windowToken, 0)

            dialogDate.show()

            true
        }

        findViewById<View>(R.id.notizen).setOnClickListener {
            main.bringToFront()
        }

        findViewById<View>(R.id.layout).setOnClickListener {
            header.bringToFront()

            dialogSubject.show()
        }

        initDateDialog()
        initSubjectDialog()

        id = intent.getIntExtra("id", 0)

        refreshData()
    }

    override fun onResume() {
        super.onResume()

        findViewById<View>(R.id.datum).clearFocus()
        findViewById<View>(R.id.notizen).clearFocus()
    }

    private fun initSubjectDialog() {
        dialogSubject = AppCompatDialog(this)
        dialogSubject.setContentView(R.layout.exams_dialog_subject_picker)

        dialogSubject.findViewById<View>(R.id.item_other)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorOther)
        dialogSubject.findViewById<View>(R.id.item_other)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_other)
        dialogSubject.findViewById<View>(R.id.item_other)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject(""),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_math)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorMath)
        dialogSubject.findViewById<View>(R.id.item_math)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_math)
        dialogSubject.findViewById<View>(R.id.item_math)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("M"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_german)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorGerman)
        dialogSubject.findViewById<View>(R.id.item_german)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_german)
        dialogSubject.findViewById<View>(R.id.item_german)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("D"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_english)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorEnglish)
        dialogSubject.findViewById<View>(R.id.item_english)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_english)
        dialogSubject.findViewById<View>(R.id.item_english)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("E"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_french)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorFrench)
        dialogSubject.findViewById<View>(R.id.item_french)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_french)
        dialogSubject.findViewById<View>(R.id.item_french)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("F"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_biology)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorBiology)
        dialogSubject.findViewById<View>(R.id.item_biology)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_biology)
        dialogSubject.findViewById<View>(R.id.item_biology)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("BI"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_chemistry)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorChemistry)
        dialogSubject.findViewById<View>(R.id.item_chemistry)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_chemistry)
        dialogSubject.findViewById<View>(R.id.item_chemistry)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("CH"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_physics)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorPhysics)
        dialogSubject.findViewById<View>(R.id.item_physics)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_physics)
        dialogSubject.findViewById<View>(R.id.item_physics)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("PH"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_CS)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorCS)
        dialogSubject.findViewById<View>(R.id.item_CS)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_cs)
        dialogSubject.findViewById<View>(R.id.item_CS)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("IF"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Politics)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorPolitics)
        dialogSubject.findViewById<View>(R.id.item_Politics)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_politics)
        dialogSubject.findViewById<View>(R.id.item_Politics)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("PK"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Religion)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorReligion)
        dialogSubject.findViewById<View>(R.id.item_Religion)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_religion)
        dialogSubject.findViewById<View>(R.id.item_Religion)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("P"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Spanish)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorSpanish)
        dialogSubject.findViewById<View>(R.id.item_Spanish)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_spanish)
        dialogSubject.findViewById<View>(R.id.item_Spanish)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("S"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Sport)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorSport)
        dialogSubject.findViewById<View>(R.id.item_Sport)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_sport)
        dialogSubject.findViewById<View>(R.id.item_Sport)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("SP"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Dutch)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorDutch)
        dialogSubject.findViewById<View>(R.id.item_Dutch)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_dutch)
        dialogSubject.findViewById<View>(R.id.item_Dutch)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("N"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Latin)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorLatin)
        dialogSubject.findViewById<View>(R.id.item_Latin)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_latin)
        dialogSubject.findViewById<View>(R.id.item_Latin)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("L"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Geography)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorGeography)
        dialogSubject.findViewById<View>(R.id.item_Geography)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_geography)
        dialogSubject.findViewById<View>(R.id.item_Geography)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("EK"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_History)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorHistory)
        dialogSubject.findViewById<View>(R.id.item_History)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_history)
        dialogSubject.findViewById<View>(R.id.item_History)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("GE"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }

        dialogSubject.findViewById<View>(R.id.item_Education)?.findViewById<ImageView>(R.id.dot)!!.setTint(R.color.colorEducation)
        dialogSubject.findViewById<View>(R.id.item_Education)?.findViewById<TextView>(R.id.title)!!.setText(R.string.exams_subject_education)
        dialogSubject.findViewById<View>(R.id.item_Education)?.setOnClickListener {
            data = Exam(
                    data.id,
                    data.datum,
                    converters.toSubject("PA"),
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            dialogSubject.dismiss()
            refreshSubject()
        }
    }

    private fun initDateDialog() {
        dialogDate = AppCompatDialog(this)
        dialogDate.setContentView(R.layout.exams_dialog_date_picker)

        dialogDate.findViewById<CalendarView>(R.id.calendar)!!.date = data.datum.time
        dialogDate.findViewById<CalendarView>(R.id.calendar)!!.setOnDateChangeListener { _, year, month, dayOfMonth ->
            newDate[Calendar.YEAR] = year
            newDate[Calendar.MONTH] = month
            newDate[Calendar.DAY_OF_MONTH] = dayOfMonth
        }

        dialogDate.findViewById<View>(R.id.buttonOk)!!.setOnClickListener {
            val date = newDate.time
            data = Exam(
                    data.id,
                    date,
                    data.fach,
                    data.kurs,
                    data.lehrer,
                    data.stufe
            )
            findViewById<TextView>(R.id.datum).text = dateFormat.format(data.datum)

            dialogDate.dismiss()
        }

        dialogDate.findViewById<View>(R.id.buttonCancel)!!.setOnClickListener {
            dialogDate.dismiss()
        }
    }

    private fun save() {
        launch(CommonPool) {

            if (id == 0) {
                DatabaseManager.getInstance(applicationContext).databaseInterface().insertExam(data)
            } else {
                DatabaseManager.getInstance(applicationContext).databaseInterface().updateExam(data)
            }

        }
    }

    private fun refreshData() {
        launch(CommonPool) {

            if (id != 0) {
                data = DatabaseManager.getInstance(applicationContext).databaseInterface().getExam(id)
            }

            runOnUiThread {
                findViewById<TextView>(R.id.datum).text = dateFormat.format(data.datum)
                dialogDate.findViewById<CalendarView>(R.id.calendar)!!.date = data.datum.time
                if (id != 0) {
                    refreshSubject()
                }
            }

        }
    }

    private fun refreshSubject() {
        findViewById<TextView>(R.id.chooseSubject).text = data.fach.name

        if (header.backgroundDrawable != null) {
            (header.backgroundDrawable!! as GradientDrawable).setColor(data.fach.color.toColor(applicationContext))
        }
    }

}