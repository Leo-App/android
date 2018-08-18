package de.slg.leoapp.core.utility

import android.content.Context
import de.slg.leoapp.core.preferences.PreferenceManager

class User(private val context: Context) {
    var id: Int = 0
        get() {
            if (field == 0) PreferenceManager.read(context) {
                field = it.getInt(PreferenceManager.User.ID, 0)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                it.putInt(PreferenceManager.User.ID, value)
            }
        }

    var userName: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = it.getString(PreferenceManager.User.NAME, "")
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                it.putString(PreferenceManager.User.NAME, value)
            }
        }

    var loginName: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = it.getString(PreferenceManager.User.NAME_DEFAULT, "")
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                it.putString(PreferenceManager.User.NAME_DEFAULT, value)
            }
        }

    var grade: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = it.getString(PreferenceManager.User.GRADE)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                it.putString(PreferenceManager.User.GRADE, value)
            }
        }

    var permission: Int = 0
        get() {
            if (field == 0) PreferenceManager.read(context) {
                field = it.getInt(PreferenceManager.User.PERMISSION, 0)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                it.putInt(PreferenceManager.User.PERMISSION, value)
            }
        }
}
