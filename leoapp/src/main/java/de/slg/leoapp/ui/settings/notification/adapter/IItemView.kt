package de.slg.leoapp.ui.settings.notification.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.slg.leoapp.core.ui.mvp.MVPView

interface IItemView : MVPView {
    fun setTitle(@StringRes title: Int)
    fun setDescription(@StringRes description: Int)
    fun setSwitchState(state: Boolean)
    fun setIcon(@DrawableRes icon: Int)
}