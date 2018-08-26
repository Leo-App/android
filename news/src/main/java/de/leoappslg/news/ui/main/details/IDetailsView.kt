package de.leoappslg.news.ui.main.details

import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.ui.mvp.MVPView

interface IDetailsView : MVPView {
    fun setInfoLine(info: String)
    fun setDate(date: String)
    fun setTitle(title: String)
    fun setContent(content: String)
    fun setProfilePicture(profilePicture: ProfilePicture)
    fun openDatePicker()
    fun enableTextViewEditing()
}