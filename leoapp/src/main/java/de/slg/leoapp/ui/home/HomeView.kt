package de.slg.leoapp.ui.home

import de.slg.leoapp.core.ui.LeoAppFeatureActivity

interface HomeView {
    fun openNavigationDrawer()
    fun closeNavigationDrawer()
    fun openFeatureActivity(activity: Class<out LeoAppFeatureActivity>)
    fun openProfile()
    fun openSettings()
}