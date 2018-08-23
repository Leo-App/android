package de.leoappslg.news.ui.main.details

import de.slg.leoapp.core.ui.mvp.MVPView

interface IDetailsView : MVPView {
    fun openDatePicker()
    fun enableTextViewEditing()
}