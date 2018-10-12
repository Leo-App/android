package de.slg.leoapp.news.ui.main.listing

import de.slg.leoapp.news.ui.main.MainActivity
import de.slg.leoapp.core.ui.mvp.MVPView

interface IListView : MVPView {
    fun showListing()
    fun getCallingActivity(): MainActivity
}