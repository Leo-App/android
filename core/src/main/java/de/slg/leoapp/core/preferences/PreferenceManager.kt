@file:Suppress("unused")

package de.slg.leoapp.core.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import de.slg.leoapp.annotation.PreferenceKey

class PreferenceManager {

    class PreferenceEditor internal constructor(private val preferences: SharedPreferences.Editor) {
        fun putBoolean(@PreferenceKey key: String, value: Boolean) {
            preferences.putBoolean(key, value)
        }
        fun putString(@PreferenceKey key: String, value: String) {
            preferences.putString(key, value)
        }
        fun putInt(@PreferenceKey key: String, value: Int) {
            preferences.putInt(key, value)
        }
        fun putFloat(@PreferenceKey key: String, value: Float) {
            preferences.putFloat(key, value)
        }
    }

    class PreferenceReader internal constructor(private val preferences: SharedPreferences) {
        fun getBoolean(@PreferenceKey key: String, default: Boolean = false) : Boolean = preferences.getBoolean(key, default)
        fun getString(@PreferenceKey key: String, default: String = "") : String = preferences.getString(key, default)!!
        fun getInt(@PreferenceKey key: String, default: Int = -1) : Int = preferences.getInt(key, default)
        fun getFloat(@PreferenceKey key: String, default: Float = -1f) : Float = preferences.getFloat(key, default)
    }

    companion object {
        fun edit(context: Context, tasks: PreferenceEditor.() -> Unit) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val writer = PreferenceEditor(editor)
            writer.tasks()
            editor.apply()
        }

        fun read(context: Context, tasks: PreferenceReader.() -> Unit) {
            val reader = PreferenceReader(PreferenceManager.getDefaultSharedPreferences(context))
            reader.tasks()
        }
    }

    abstract class User {
        companion object {
            @PreferenceKey
            const val FIRST_NAME: String = "preference_key_user_name"

            @PreferenceKey
            const val LAST_NAME: String = "preference_key_user_name"

            @PreferenceKey
            const val ID: String = "preference_key_user_id"

            @PreferenceKey
            const val GRADE: String = "preference_key_user_klasse"

            @PreferenceKey
            const val NAME_DEFAULT: String = "preference_key_user_defaultname"

            @PreferenceKey
            const val PERMISSION: String = "preference_key_user_permission"

            @PreferenceKey
            const val PROFILE_PICTURE_URL: String = "preference_key_user_picture"
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

}
