package de.slg.leoapp.ui.home

import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.ui.mvp.MVPView

interface HomeView : MVPView {
    fun openFeatureActivity(activity: Class<out LeoAppFeatureActivity>)
    fun showFeatureList()
}