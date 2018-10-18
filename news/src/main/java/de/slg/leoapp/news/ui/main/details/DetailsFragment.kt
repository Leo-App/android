package de.slg.leoapp.news.ui.main.details

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.news.R
import de.slg.leoapp.news.ui.main.MainActivity
import kotlinx.android.synthetic.main.news_fragment_details.*
import java.util.*

class DetailsFragment(private val presenter: DetailsPresenter) : Fragment(), IDetailsView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onViewAttached(this)
        return inflater.inflate(R.layout.news_fragment_details, container, false)
    }

    override fun openDatePicker(currentDeadline: Calendar) {
        val year = currentDeadline.get(Calendar.YEAR)
        val month = currentDeadline.get(Calendar.MONTH)
        val day = currentDeadline.get(Calendar.DAY_OF_MONTH)

        //If the user selected a date, we send the result back to our presenter
        DatePickerDialog(context!!, { _, y, m, d ->
            currentDeadline.set(y, m - 1, d, 0, 0)
            presenter.onDatePickerDateSelected(Date(currentDeadline.timeInMillis))
        }, year, month, day).show()
    }

    override fun enableTextViewEditing() {
        //todo include dateedit
        contentView.visibility = View.GONE
        contentEdit.setText(contentView.text, TextView.BufferType.EDITABLE)
        textInput.visibility = View.VISIBLE
    }

    override fun disableTextViewEditing() {
        //todo include dateedit
        contentView.visibility = View.VISIBLE
        contentView.text = contentEdit.text
        textInput.visibility = View.GONE
    }

    override fun setInfoLine(info: String) {
        infoView.text = info
    }

    override fun setDate(date: String) {
        deadlineView.text = date
    }

    override fun setTitle(title: String) {
        titleView.text = title
    }

    override fun setContent(content: String) {
        contentView.text = content
    }

    override fun getEditedContent(): String {
        return contentEdit.text.toString()
    }

    override fun getEditedDate(): Date {
        TODO("not implemented")
    }

    override fun setProfilePicture(profilePicture: ProfilePicture) {
        //At this point the user should have already synchronized the profile picture - if not, they get a placeholder
        authorPicture.setImageBitmap(profilePicture.getPictureOrPlaceholder())
    }

    override fun getViewContext() = context!!

    override fun getCallingActivity() = activity as MainActivity
}