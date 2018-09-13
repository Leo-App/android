package de.slg.leoapp.utils

import android.app.Activity
import android.view.View

interface Animation {
    fun revealActivity(x: Int, y: Int, rootLayout: View)
    fun hideActivity(x: Int, y: Int, rootLayout: View, activity: Activity)
}