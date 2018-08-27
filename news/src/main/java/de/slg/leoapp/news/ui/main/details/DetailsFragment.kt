package de.slg.leoapp.news.ui.main.details

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.news.R
import java.util.*

class DetailsFragment(private val presenter: DetailsPresenter) : Fragment(), IDetailsView {

    //Views
    private lateinit var titleView: TextView
    private lateinit var infoView: TextView
    private lateinit var dateView: TextView
    private lateinit var contentView: TextView
    private lateinit var imageView: ImageView
    private lateinit var textInput

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onViewAttached(this)
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleView = view.findViewById(R.id.title)
        infoView = view.findViewById(R.id.info)
        dateView = view.findViewById(R.id.deadline)
        contentView = view.findViewById(R.id.content)
        imageView = view.findViewById(R.id.authorPicture)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun openDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        //If the user selected a date, we send the result back to our presenter
        DatePickerDialog(context!!, { _, y, m, d ->
            c.set(y, m - 1, d, 0, 0)
            presenter.onDatePickerDateSelected(Date(c.timeInMillis))
        }, year, month, day).show()
    }

    override fun enableTextViewEditing() {
        //todo hide textview, fill edittext with entrytext then show edittext
    }

    override fun setInfoLine(info: String) {
        infoView.text = info
    }

    override fun setDate(date: String) {
        dateView.text = date
    }

    override fun setTitle(title: String) {
        titleView.text = title
    }

    override fun setContent(content: String) {
        contentView.text = content
    }

    override fun setProfilePicture(profilePicture: ProfilePicture) {
        //At this point the user should have already synchronized the profile picture - if not, they get a placeholder
        imageView.setImageBitmap(profilePicture.getPictureOrPlaceholder())
    }

    override fun getViewContext() = context!!
}