package de.slg.leoapp.ui.profile

import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class ProfileActivity : LeoAppFeatureActivity(), ProfileView {
    override fun usesActionButton() = false

    override fun getActionIcon() = 0

    override fun getContentView() = R.layout.activity_profile

    override fun getNavigationHighlightId() = -1

    override fun getActivityTag() = "leoapp_feature_profile"

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

}