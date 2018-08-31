package de.slg.leoapp.core.utility

import androidx.appcompat.app.AppCompatActivity
import de.slg.leoapp.annotation.BindView

class ViewBinder {
    companion object {
        @JvmStatic
        fun bind(activity: AppCompatActivity) {
            for (field in activity.javaClass.declaredFields) {
                field.isAccessible = true
                val annotation = field.getAnnotation(BindView::class.java) ?: continue
                field.set(activity, activity.findViewById(annotation.viewid))
            }
        }
    }
}