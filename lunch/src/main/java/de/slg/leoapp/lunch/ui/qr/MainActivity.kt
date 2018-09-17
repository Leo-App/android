package de.slg.leoapp.lunch.ui.qr

import android.view.View
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.ui.image.BackgroundEffect
import de.slg.leoapp.core.ui.image.Blur
import de.slg.leoapp.lunch.R

class MainActivity : LeoAppFeatureActivity(), BackgroundEffect by Blur(R.id.background_image) {

    override fun getContentView() = R.layout.activity_lunch

    override fun getNavigationHighlightId() = R.string.lunch_feature_name

    override fun getActivityTag() = "lunch_feature_main"

    override fun getActionIcon() = R.drawable.ic_scanning

    override fun getAction() = { _: View ->
        //TODO
    }

}