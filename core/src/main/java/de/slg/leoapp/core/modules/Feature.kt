package de.slg.leoapp.core.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.slg.leoapp.core.ui.LeoAppFeatureActivity

interface Feature : Module {
    @DrawableRes
    fun getIcon(): Int

    @StringRes
    fun getName(): Int

    fun getFeatureId(): Int

    fun getNecessaryPermission(): Int

    fun getEntryActivity(): Class<out LeoAppFeatureActivity>
}