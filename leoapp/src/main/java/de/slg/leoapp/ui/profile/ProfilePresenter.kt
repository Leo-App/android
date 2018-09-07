package de.slg.leoapp.ui.profile

import android.graphics.Bitmap
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.data.User
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class ProfilePresenter : AbstractPresenter<ProfileView, Unit>(), IProfilePresenter {

    private var editing: Boolean = false
    private lateinit var user: User
    private lateinit var currentProfilePicture: Bitmap

    override fun onViewAttached(view: ProfileView) {
        super.onViewAttached(view)
        user = User(getMvpView().getViewContext())
        getMvpView().setName("${user.firstName} ${user.lastName}")
        getMvpView().setGrade(user.grade)
        getMvpView().setLoginName(user.loginName)
        //TODO set profile picture and save in currentProfilePicture
    }

    override fun onBackPressed() {
        if (editing) {
            getMvpView().disableTextViewEditing()
            getMvpView().hideImageViewEditOverlay()
            getMvpView().setName("${user.firstName} ${user.lastName}")
            getMvpView().setProfilePicture(user.profilePicture.getPictureOrPlaceholder())
        } else {
            getMvpView().terminate()
        }
    }

    override fun onEditStarted() {
        editing = true
        getMvpView().enableTextViewEditing()
        getMvpView().showImageViewEditOverlay()
    }

    override fun onEditFinished() {
        //TODO update changes remote
        val enteredNames = getMvpView().getName().split(" ")

        if (enteredNames.size < 2) {
            getMvpView().showInvalidNameError()
            return
        }

        user.firstName = with (enteredNames) { slice(0..size-2).joinToString(separator = " ") }
        user.lastName = enteredNames[enteredNames.size-1]
        user.profilePicture = ProfilePicture(currentProfilePicture, user.id)
        getMvpView().disableTextViewEditing()
        getMvpView().hideImageViewEditOverlay()
        editing = false
    }

    override fun onImageInteraction() {
        if (editing) {
            getMvpView().openImageSelectionDialog()
        }
    }

    override fun onImageSelected(bitmap: Bitmap) {
        currentProfilePicture = bitmap
        getMvpView().setProfilePicture(bitmap)
    }
}