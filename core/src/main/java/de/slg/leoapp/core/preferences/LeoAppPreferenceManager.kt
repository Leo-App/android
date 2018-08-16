@file:Suppress("unused")

package de.slg.leoapp.core.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import de.slg.leoapp.annotation.PreferenceKey

class LeoAppPreferenceManager {
    abstract class User {
        companion object {
            @PreferenceKey
            const val NAME: String = "preference_key_user_name"

            @PreferenceKey
            const val ID: String = "preference_key_user_id"

            @PreferenceKey
            const val KLASSE: String = "preference_key_user_klasse"

            @PreferenceKey
            const val NAME_DEFAULT: String = "preference_key_user_defaultname"

            @PreferenceKey
            const val PERMISSION: String = "preference_key_user_permission"
        }
    }

    abstract class Device {
        companion object {
            @PreferenceKey
            const val AUTHENTICATION: String = "preference_key_device_authentication"

            @PreferenceKey
            const val NAME: String = "preference_key_device_name"
        }
    }

    companion object {
        fun editPreference(context: Context, key: @PreferenceKey String, value: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(key, value)
                    .apply()
        }

        fun editPreference(context: Context, key: @PreferenceKey String, value: Int) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt(key, value)
                    .apply()
        }

        fun editPreference(context: Context, key: @PreferenceKey String, value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(key, value)
                    .apply()
        }

        fun editPreference(context: Context, key: @PreferenceKey String, value: Float) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putFloat(key, value)
                    .apply()
        }

        fun editPreference(context: Context, key: @PreferenceKey String, value: Long) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putLong(key, value)
                    .apply()
        }

        fun editPreferences(context: Context, data: Array<Pair<@PreferenceKey String, String>>) {
            val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            for (p in data) {
                editor.putString(p.first, p.second)
            }
            editor.apply()
        }
    }
}
