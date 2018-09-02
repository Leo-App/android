package de.slg.leoapp.ui.home

import android.content.Intent
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.slg.leoapp.R
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.Utils

class HomeActivity : LeoAppFeatureActivity(), HomeView {
    override fun getContentView() = R.layout.activity_home

    override fun getActivityTag() = "leoapp_feature_home"

    override fun getNavigationHighlightId(): Int {
        return R.string.home
    }

    override fun openNavigationDrawer() {

    }

    override fun closeNavigationDrawer() {

    }

    override fun openFeatureActivity(activity: Class<out LeoAppFeatureActivity>) {
        startActivity(Intent(applicationContext, activity))
    }

    override fun openProfile() {
        startActivity(Intent(applicationContext, Utils.Activity.getProfileReference()))
    }

    override fun openSettings() {
        startActivity(Intent(applicationContext, Utils.Activity.getSettingsReference()))
    }
}