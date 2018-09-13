package de.slg.leoapp.core.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class CircularImageView : ImageView {

    private var canvasSize = 0
    private var overlay: Bitmap? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        canvasSize = Math.min(width, height)

        drawable ?: return
        if (width == 0 || height == 0) return

        canvas.drawBitmap(getCircularBitmap((drawable as BitmapDrawable).bitmap, canvasSize), 0f, 0f, null)
        if (overlay != null) {
            canvas.drawBitmap(getCircularBitmap(overlay!!, canvasSize), 0f, 0f, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    fun setOverlay(bitmap: Bitmap?) {
        overlay = bitmap
        invalidate()
    }

    private fun getCircularBitmap(bitmap: Bitmap, radius: Int): Bitmap {
        val finalBitmap = scaleCenterCrop(bitmap, radius, radius)
        val output = Bitmap.createBitmap(finalBitmap.width, finalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, finalBitmap.width, finalBitmap.height)

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(finalBitmap.width / 2 + 0.7f, finalBitmap.height / 2 + 0.7f, finalBitmap.width / 2 + 0.1f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(finalBitmap, rect, rect, paint)

        return output
    }

    private fun measureWidth(measureSpec: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        result = when (specMode) {
            View.MeasureSpec.EXACTLY,
            View.MeasureSpec.AT_MOST ->
                specSize
            View.MeasureSpec.UNSPECIFIED -> canvasSize
            else -> canvasSize

        }

        return result
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpecHeight)
        val specSize = View.MeasureSpec.getSize(measureSpecHeight)

        result = when (specMode) {
            View.MeasureSpec.EXACTLY,
            View.MeasureSpec.AT_MOST ->
                specSize
            View.MeasureSpec.UNSPECIFIED -> canvasSize
            else ->
                canvasSize
        }

        return result + 2
    }

    private fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        val sourceWidth = source.width
        val sourceHeight = source.height

        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(newWidth, newHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)

        return dest
    }

}