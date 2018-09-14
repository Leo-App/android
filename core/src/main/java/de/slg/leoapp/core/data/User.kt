package de.slg.leoapp.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.slg.leoapp.core.preferences.PreferenceManager
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import java.net.URL

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
            //If profilePicture is still default, update it from preferences
            if (field.getURLString() == "") PreferenceManager.read(context) {
                field = ProfilePicture(getString(PreferenceManager.User.PROFILE_PICTURE_URL))
            }

            //If profilePicture has not yet been synchronized but we have one in cache, we return that.
            if (field.getPictureOrNull() == null && Cache.profilePicture != null) {
                return ProfilePicture(Cache.profilePicture!!, id)
            }

            return field
        }
        set(value) {
            field = value

            //If the profilePicture URLs differ, we update our preferences
            if (value.getURLString() != field.getURLString()) {
                PreferenceManager.edit(context) {
                    putString(PreferenceManager.User.PROFILE_PICTURE_URL, value.getURLString())
                }
            }

            //We set our cache to the new picture or to null
            //TODO that means if the new profilePic is not based on a local image - but on a URL - we use a placeholder during the sync time
            //TODO decide if we want that. If not, only update if getPictureOrNull != null.
            //TODO In that case the previous cached image will be used during syncing.
            Cache.profilePicture = value.getPictureOrNull()

            if (Cache.profilePicture == null) {
                //If the cache was set to null previously, we sync a picture from the supplied URL to cache
                launch(UI) {
                    Cache.profilePicture = async(CommonPool) {
                        try {
                            BitmapFactory.decodeStream(URL(value.getURLString()).openStream())
                        } catch (e: IOException) {
                            Log.d("leoapp", "IOEXCEPTION")
                            value.getPictureOrPlaceholder()
                        }
                    }.await()
                }
            }
        }

    private companion object Cache {
        var profilePicture: Bitmap? = null
    }

    fun getFullName(): String = "$firstName $lastName"

}
