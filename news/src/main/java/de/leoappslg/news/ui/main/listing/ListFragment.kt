package de.leoappslg.news.ui.main.listing

import android.content.Context
import androidx.fragment.app.Fragment

class ListFragment: Fragment(), IListView {
    override fun getViewContext() = context!!
}