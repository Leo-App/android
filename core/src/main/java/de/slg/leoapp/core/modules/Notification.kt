package de.slg.leoapp.core.modules

import androidx.annotation.StringRes
import de.slg.leoapp.annotation.PreferenceKey

data class Notification(@StringRes val description: Int, @PreferenceKey val preferenceKey: String)
