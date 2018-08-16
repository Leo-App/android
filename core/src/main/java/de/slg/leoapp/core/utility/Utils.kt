@file:Suppress("unused", "WeakerAccess")

package de.slg.leoapp.core.utility

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import de.leoappslg.core.datastructure.Stack
import de.leoappslg.core.preferences.LeoAppPreferenceManager

abstract class Utils {
    companion object {
        fun setup(context: Context) {
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            User.id = preferences.getInt(LeoAppPreferenceManager.User.ID, User.id)
            User.permission = preferences.getInt(LeoAppPreferenceManager.User.PERMISSION, User.permission)
            User.name = preferences.getString(LeoAppPreferenceManager.User.NAME, User.name)
            User.defaultname = preferences.getString(LeoAppPreferenceManager.User.NAME_DEFAULT, User.defaultname)
            User.klasse = preferences.getString(LeoAppPreferenceManager.User.KLASSE, User.klasse)
        }
    }

    abstract class User {
        companion object {
            var id: Int = 0
            var permission: Int = 0
            var name: String = ""
            var defaultname: String = ""
            var klasse: String = ""
        }
    }

    abstract class Activity {
        companion object {
            private val openActivities: Stack<String> = Stack()

            fun registerActivity(tag: String) {
                openActivities.add(tag)
            }

            fun unregisterActivity(tag: String) {
                if (tag == openActivities.getContent()) {
                    openActivities.remove()
                }
            }
        }
    }
}
