package de.slg.leoapp.core.modules

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface MenuEntry {

    fun getId(): Int

    @StringRes
    fun getTitle(): String

    @DrawableRes
    fun getIcon(): Int

    fun getIntent(context: Context): Intent
}
