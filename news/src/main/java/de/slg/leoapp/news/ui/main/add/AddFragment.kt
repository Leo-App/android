package de.slg.leoapp.news.ui.main.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.slg.leoapp.news.R
import kotlinx.android.synthetic.main.fragment_add.*
import java.time.LocalDate
import java.util.*

class AddFragment(private val presenter: AddPresenter) : Fragment(), IAddView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onViewAttached(this)
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun getEnteredTitle() = editTitle.text.toString()

    override fun getEnteredContent() = editContent.text.toString()

    override fun getSelectedRecipient(): String {
        TODO("not implemented")
    }

    override fun getSelectedDeadline() = editDate.text.toString()

    override fun getViewContext() = context!!
}