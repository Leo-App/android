package de.slg.leoapp.core.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import android.content.Context
import android.content.Intent

/**
 * @author Moritz
 * Erstelldatum: 17.08.2018
 */
interface MenuEntry {

    fun getId(): Int

    @StringRes
    fun getTitle(): String

    @DrawableRes
    fun getIcon(): Int

    fun getIntent(context: Context): Intent
}
