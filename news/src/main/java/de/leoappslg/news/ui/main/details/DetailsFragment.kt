package de.leoappslg.news.ui.main.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class DetailsFragment : Fragment(), IDetailsView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun openDatePicker() {
        TODO("not implemented")
    }

    override fun enableTextViewEditing() {
        TODO("not implemented")
    }

    override fun getViewContext() = context!!
}