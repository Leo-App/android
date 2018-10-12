package de.slg.leoapp.ui.home.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface FeatureView {
    fun setName(@StringRes text: Int)
    fun setIcon(@DrawableRes icon: Int)
}