package de.leoapp_slg.core.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.leoapp_slg.core.activity.LeoAppFeatureActivity

abstract class Feature {
    @DrawableRes
    abstract fun getIcon(): Int

    @StringRes
    abstract fun getName(): String

    abstract fun getNecessaryPermission(): Int

    abstract fun getEntryActivity(): Class<out LeoAppFeatureActivity>
}