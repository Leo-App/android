package de.slg.leoapp.ui.profile

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class ProfileActivity : LeoAppFeatureActivity(), ProfileView {

    override fun getContentView(): Int {
        TODO("not implemented")
    }

    override fun getNavigationHighlightId(): Int {
        return -1
    }

    override fun getActivityTag(): String {
        return "leoapp_feature_profile"
    }

    override fun showImageViewEditOverlay() {
        TODO("not implemented")
    }

    override fun hideImageViewEditOverlay() {
        TODO("not implemented")
    }

    override fun showSelectImageDialog() {
        TODO("not implemented")
    }

    override fun enableTextViewEditing() {
        TODO("not implemented")
    }

    override fun disableTextViewEditing() {
        TODO("not implemented")
    }

    override fun showEditButton() {
        TODO("not implemented")
    }

    override fun showSaveButton() {
        TODO("not implemented")
    }

    override fun saveChanges() {
        TODO("not implemented")
    }

}