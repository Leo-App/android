package de.slg.leoapp.ui.settings.notification

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
            if (cur.hasNotification()) {
                val notification = cur.getNotification() ?: continue

                var isEnabled = false
                PreferenceManager.read(getMvpView().getViewContext()) {
                    isEnabled = getBoolean(notification.preferenceKey)
                }

                list.add(NotificationSetting(
                        cur.getIcon(), cur.getName(), notification.description, notification.preferenceKey, isEnabled))
            }
        }
    }

    override fun getNotificationAmount() = list.size

    override fun onViewAttached(view: INotificationView) {
        super.onViewAttached(view)
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
                putBoolean(cur.preferenceKey, cur.enabled)
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