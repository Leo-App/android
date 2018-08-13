package de.leoappslg.core.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.leoappslg.core.activity.LeoAppFeatureActivity

interface Feature : Module {
    @DrawableRes
    fun getIcon(): Int

    @StringRes
    fun getName(): String

    fun getNecessaryPermission(): Int

    fun getEntryActivity(): Class<out LeoAppFeatureActivity>
}