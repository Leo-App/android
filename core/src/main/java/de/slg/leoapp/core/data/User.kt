package de.slg.leoapp.core.data

import android.content.Context
import androidx.annotation.NonNull
import de.slg.leoapp.core.preferences.PreferenceManager

class User(private val context: Context) {
    var id: Int = 0
        get() {
            if (field == 0) PreferenceManager.read(context) {
                field = getInt(PreferenceManager.User.ID, 0)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putInt(PreferenceManager.User.ID, value)
            }
        }

    var firstName: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = getString(PreferenceManager.User.FIRST_NAME, "")
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putString(PreferenceManager.User.FIRST_NAME, value)
            }
        }

    var lastName: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = getString(PreferenceManager.User.LAST_NAME, "")
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putString(PreferenceManager.User.LAST_NAME, value)
            }
        }

    var loginName: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = getString(PreferenceManager.User.NAME_DEFAULT, "")
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putString(PreferenceManager.User.NAME_DEFAULT, value)
            }
        }

    var grade: String = ""
        get() {
            if (field == "") PreferenceManager.read(context) {
                field = getString(PreferenceManager.User.GRADE)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putString(PreferenceManager.User.GRADE, value)
            }
        }

    var permission: Int = 0
        get() {
            if (field == 0) PreferenceManager.read(context) {
                field = getInt(PreferenceManager.User.PERMISSION, 0)
            }
            return field
        }
        set(value) {
            field = value
            PreferenceManager.edit(context) {
                putInt(PreferenceManager.User.PERMISSION, value)
            }
        }

    var profilePicture: ProfilePicture = ProfilePicture("")
        get() {
            if (field.getURLString() == "") PreferenceManager.read(context) {
                field = ProfilePicture(getString(PreferenceManager.User.PROFILE_PICTURE_URL))
            }
            return field
        }
        set(value) {
            if (value.getURLString() == field.getURLString()) return

            field = value
            PreferenceManager.edit(context) {
                putString(PreferenceManager.User.PROFILE_PICTURE_URL, value.getURLString())
            }
        }
}
