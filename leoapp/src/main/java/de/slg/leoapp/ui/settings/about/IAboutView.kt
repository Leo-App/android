package de.slg.leoapp.ui.settings.about

import de.slg.leoapp.core.ui.mvp.MVPView

interface IAboutView : MVPView {
    fun openDialog()
    fun sendToast(text: String)
    fun openWebpage()
    fun moveToOverview()
}