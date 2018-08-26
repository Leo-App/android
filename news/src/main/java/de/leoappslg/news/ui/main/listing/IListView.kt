package de.leoappslg.news.ui.main.listing

import de.leoappslg.news.ui.main.MainActivity
import de.slg.leoapp.core.ui.mvp.MVPView

interface IListView : MVPView {
    fun showListing()
    fun getCallingActivity(): MainActivity
}