package de.slg.leoapp.core.data

import android.graphics.*
import de.slg.leoapp.core.utility.URL_PROFILE_PICTURE
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import java.net.URL

class ProfilePicture(private val imageURL: String, private val callback: (Bitmap) -> Unit = {}) {

    private lateinit var bitmap: Bitmap

    constructor(bitmap: Bitmap, userId: Int) : this(URL_PROFILE_PICTURE.format(userId)) {
        this.bitmap = bitmap
        launch(CommonPool) {
            //TODO make multipart POST request to upload profile picture
        }
    }

    init {
        launch(UI) {
            bitmap = async(CommonPool) {
                try {
                    BitmapFactory.decodeStream(URL(imageURL).openConnection().getInputStream())
                } catch (e: IOException) {
                    getReplacement()
                }
            }.await()
            callback(bitmap)
        }
    }

    fun getPictureOrNull(): Bitmap? {
        if (::bitmap.isInitialized)
            return bitmap
        return null
    }

    fun getPictureOrPlaceholder(): Bitmap {
        if (::bitmap.isInitialized)
            return bitmap
        return getReplacement()
    }

    fun getURLString(): String {
        return imageURL
    }

    private fun getReplacement(): Bitmap {
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.GRAY
        canvas.drawRect(0f, 0f, 50f, 50f, paint)
        return bitmap
    }

}