package de.slg.leoapp.core.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import de.slg.leoapp.core.R
import java.io.BufferedReader
import java.io.File
import android.util.TypedValue


//File
fun File.contains(s: String): Boolean {
    val reader = inputStream().bufferedReader()
    return reader.use(BufferedReader::readText).contains(s)
}

//ByteArray
fun ByteArray.toHexString(): String {
    val hexArray = "0123456789abcdef".toCharArray()
    val hexChars = CharArray(size * 2)
    for (j in 0 until size) {
        val v = 0xFF and get(j).toInt()
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

//ImageView
fun ImageView.setTint(@ColorRes color: Int) {
    setColorFilter(ContextCompat.getColor(context!!, color), PorterDuff.Mode.MULTIPLY)
}

//Resources
fun Int.toDrawable(context: Context): Drawable {
    return ResourcesCompat.getDrawable(context.resources, this, null)
            ?: throw Resources.NotFoundException("$this is not a valid resource id")
}

fun Int.toBitmap(context: Context): Bitmap {
    return BitmapFactory.decodeResource(context.resources, this)
            ?: throw Resources.NotFoundException("$this is not a valid resource id")
}

//View
fun Int.dpValue(context: Context): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
    )
}

fun Float.pxValue(context: Context): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            this,
            context.resources.displayMetrics
    ).toInt()
}

//Arithmetic
@Suppress("implicit_cast_to_any")
inline infix fun <reified R : Number> Number.pow(exponent: Number): R {
    with(Math.pow(this.toDouble(), exponent.toDouble())) {
        return when (R::class.java.typeName) {
            Float.type() -> toFloat()
            Int.type() -> toInt()
            Long.type() -> toLong()
            Short.type() -> toShort()
            Byte.type() -> toByte()
            else -> {/* exhaustive */
            }
        } as R
    }
}

fun Any.type(): String {
    return this::class.java.typeName
}