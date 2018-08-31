package de.slg.leoapp.news.ui.main.details

import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.ui.mvp.MVPView
import de.slg.leoapp.news.ui.main.MainActivity
import java.util.*

interface IDetailsView : MVPView {
    fun setInfoLine(info: String)
    fun setDate(date: String)
    fun setTitle(title: String)
    fun setContent(content: String)
    fun setProfilePicture(profilePicture: ProfilePicture)
    fun openDatePicker(currentDeadline: Calendar)
    fun enableTextViewEditing()
    fun disableTextViewEditing()
    fun getCallingActivity(): MainActivity
    fun getEditedContent(): String
    fun getEditedDate(): Date
}