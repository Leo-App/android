package de.slg.leoapp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator

object Circular : Animation {
    override fun revealActivity(x: Int, y: Int, rootLayout: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(rootLayout.width, rootLayout.height) * 1.1).toFloat()

            ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0f, finalRadius).apply {
                duration = 550
                interpolator = AccelerateInterpolator()
                rootLayout.visibility = View.VISIBLE
                start()
            }
        }
    }

    override fun hideActivity(x: Int, y: Int, rootLayout: View, activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(rootLayout.width, rootLayout.height) * 1.1).toFloat()
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, x, y, finalRadius, 0f)

            circularReveal.duration = 550
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rootLayout.visibility = View.INVISIBLE
                    activity.finish()
                }
            })

            circularReveal.start()
        }
    }
}