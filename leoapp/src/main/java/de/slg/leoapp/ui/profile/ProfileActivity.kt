package de.slg.leoapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.toBitmap
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : LeoAppFeatureActivity(), ProfileView {

    private lateinit var presenter: ProfilePresenter

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        presenter = ProfilePresenter()
        presenter.onViewAttached(this)
        arrow_back.setOnClickListener { presenter.onBackPressed() }
    }

    override fun getContentView() = R.layout.activity_profile

    override fun getNavigationHighlightId() = -1

    override fun getActivityTag() = "leoapp_feature_profile"

    override fun getViewContext() = applicationContext!!

    override fun showImageViewEditOverlay() {
        profile_picture.setOverlay(R.mipmap.edit_overlay.toBitmap(applicationContext))
    }

    override fun hideImageViewEditOverlay() {
        profile_picture.setOverlay(null)
    }

    override fun enableTextViewEditing() {
        full_name.visibility = View.INVISIBLE
        full_name_edit.setText(full_name.text, TextView.BufferType.EDITABLE)
        full_name_edit.visibility = View.VISIBLE
        full_name_edit.requestFocus()
    }

    override fun disableTextViewEditing() {
        full_name.visibility = View.VISIBLE
        full_name_edit.visibility = View.GONE
    }

    override fun showEditButton() {
        getAppBar().replaceMenu(R.menu.app_toolbar_edit)
        getAppBar().addMenuAction(R.id.action_edit) {
            presenter.onEditStarted()
        }
    }

    override fun showSaveButton() {
        getAppBar().replaceMenu(R.menu.app_toolbar_save)
        getAppBar().addMenuAction(R.id.action_save) {
            presenter.onEditFinished()
        }
    }

    override fun getName(): String {
        return full_name.text.toString()
    }

    override fun showInvalidNameError() {
        //todo maybe "shake" name edittext?
    }

    override fun setProfilePicture(picture: Bitmap) {
        profile_picture.setImageBitmap(picture)
    }

    override fun setName(name: String) {
        full_name.text
    }

    override fun setLoginName(name: String) {
        login_name.text = name
    }

    override fun setGrade(value: String) {
        grade.text = value
    }

    override fun openImageSelectionDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 0xD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0xD && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                presenter.onImageSelected(bitmap)
            }
        }
    }

    override fun terminate() {
        finish()
    }
}