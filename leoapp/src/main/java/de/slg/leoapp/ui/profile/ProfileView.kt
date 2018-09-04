package de.slg.leoapp.ui.profile

interface ProfileView {
    fun showImageViewEditOverlay()
    fun hideImageViewEditOverlay()
    fun showSelectImageDialog()
    fun enableTextViewEditing()
    fun disableTextViewEditing()
    fun showEditButton()
    fun showSaveButton()
}