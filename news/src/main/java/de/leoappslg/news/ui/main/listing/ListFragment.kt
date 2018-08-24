package de.leoappslg.news.ui.main.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.leoappslg.news.ui.main.MainActivity
import de.leoappslg.news.ui.main.listing.adapter.ListAdapter
import de.slg.leoapp.news.R

class ListFragment(private val presenter: ListPresenter) : Fragment(), IListView {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onViewAttached(this)
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.entryListing)
    }

    override fun showListing() {
        val adapter = ListAdapter(presenter)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }


    override fun getViewContext() = context!!

    override fun getCallingActivity() = activity as MainActivity
}