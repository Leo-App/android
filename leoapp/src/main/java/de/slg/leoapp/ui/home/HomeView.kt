package de.slg.leoapp.ui.home

import android.content.Intent

interface HomeView {
    fun openNavigationDrawer()
    fun closeNavigationDrawer()
    fun openFeatureActivity(intent: Intent)
    fun openProfile()
    fun openSettings()
}