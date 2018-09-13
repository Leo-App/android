package de.slg.leoapp.data

import android.graphics.Bitmap

interface IUserDataManager {
    fun updateUsername(firstName: String, lastName: String)
    fun updateProfilePicture(picture: Bitmap)
}