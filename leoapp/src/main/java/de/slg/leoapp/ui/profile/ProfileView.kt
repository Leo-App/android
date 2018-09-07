package de.slg.leoapp.ui.profile

import android.graphics.Bitmap
import de.slg.leoapp.core.ui.mvp.MVPView

interface ProfileView : MVPView {
    fun showImageViewEditOverlay()
    fun hideImageViewEditOverlay()
    fun enableTextViewEditing()
    fun disableTextViewEditing()
    fun showEditButton()
    fun showSaveButton()
    fun setProfilePicture(picture: Bitmap)
    fun setName(name: String)
    fun setLoginName(name: String)
    fun openImageSelectionDialog()
    fun setGrade(value: String)
    fun getName(): String
    fun showInvalidNameError()
    fun terminate()
}