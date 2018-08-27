package de.slg.leoapp.news.ui.main.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.slg.leoapp.news.R
import de.slg.leoapp.news.ui.main.MainActivity
import de.slg.leoapp.news.ui.main.listing.adapter.ListAdapter
import kotlinx.android.synthetic.main.fragment_listing.*

class ListFragment(private val presenter: ListPresenter) : Fragment(), IListView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onViewAttached(this)
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun showListing() {
        val adapter = ListAdapter(presenter)

        entryListing.adapter = adapter
        entryListing.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    override fun getViewContext() = context!!

    override fun getCallingActivity() = activity as MainActivity

}