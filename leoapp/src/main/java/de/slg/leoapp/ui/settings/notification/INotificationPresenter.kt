package de.slg.leoapp.ui.settings.notification

import de.slg.leoapp.ui.settings.notification.adapter.IItemView

interface INotificationPresenter {
    fun onNotificationToggled(position: Int)
    fun onBackPressed()
    fun onBindNotificationViewAtPosition(position: Int, view: IItemView)
    fun onQuit()
    fun getNotificationAmount(): Int
}