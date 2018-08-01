@file:Suppress("unused")

package de.leoapp_slg.core.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class LeoAppPreferenceManager {
    abstract class User {
        companion object {
            @PreferenceKey
            val NAME: String = "preference_key_user_name"
            @PreferenceKey
            val ID: String = "preference_key_user_id"
            @PreferenceKey
            val KLASSE: String = "preference_key_user_klasse"
            @PreferenceKey
            val NAME_DEFAULT: String = "preference_key_user_defaultname"
        }
    }

    abstract class Device {
        companion object {
            @PreferenceKey
            val AUTHENTICATION: String = "preference_key_device_authentication"
            @PreferenceKey
            val NAME: String = "preference_key_device_name"
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
            for (p: Pair<@PreferenceKey String, String> in data) {
                editor.putString(p.first, p.second)
            }
            editor.apply()
        }
    }
}

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
private annotation class PreferenceKey
