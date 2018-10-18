package de.slg.leoapp.news.ui.main.listing

import de.slg.leoapp.core.ui.mvp.MVPView
import de.slg.leoapp.news.ui.main.MainActivity

interface IListView : MVPView {
    fun showListing()
    fun getCallingActivity(): MainActivity
}