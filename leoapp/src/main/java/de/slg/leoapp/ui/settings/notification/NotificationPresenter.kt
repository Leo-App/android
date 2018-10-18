package de.slg.leoapp.ui.settings.notification

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.slg.leoapp.ModuleLoader
import de.slg.leoapp.annotation.PreferenceKey
import de.slg.leoapp.core.preferences.PreferenceManager
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.ui.settings.notification.adapter.IItemView

class NotificationPresenter : AbstractPresenter<INotificationView, Unit>(), INotificationPresenter {

    private val list: List<NotificationSetting>

    init {
        list = mutableListOf()
        for (cur in ModuleLoader.getFeatures()) {
            val notification = cur.getNotification() ?: continue
            list.add(NotificationSetting(
                    cur.getIcon(), cur.getName(), notification.description, notification.preferenceKey, false))
        }
    }

    override fun getNotificationAmount() = list.size

    override fun onViewAttached(view: INotificationView) {
        super.onViewAttached(view)
        init()
        getMvpView().showNotificationListing()
    }

    override fun onNotificationToggled(position: Int) {
        list[position].enabled = !list[position].enabled
    }

    override fun onBackPressed() {
        getMvpView().moveToOverview()
    }

    override fun onBindNotificationViewAtPosition(position: Int, view: IItemView) {
        with(list[position]) {
            view.setTitle(title)
            view.setDescription(description)
            view.setIcon(icon)
            view.setSwitchState(enabled)
        }
    }

    override fun onQuit() {
        PreferenceManager.edit(getMvpView().getViewContext()) {
            for (cur in list) {
                Log.d("leoapp", cur.enabled.toString())
                putBoolean(cur.preferenceKey, cur.enabled)
            }
        }
    }

    private fun init() {
        for (cur in list) {
            PreferenceManager.read(getMvpView().getViewContext()) {
                cur.enabled = getBoolean(cur.preferenceKey)
            }
        }
    }

    private data class NotificationSetting(
            @DrawableRes val icon: Int,
            @StringRes val title: Int,
            @StringRes val description: Int,
            @PreferenceKey val preferenceKey: String,
            var enabled: Boolean
    )

}