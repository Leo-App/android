package de.slg.leoapp.ui.settings.notification

import de.slg.leoapp.core.ui.mvp.MVPView

interface INotificationView : MVPView {
    fun moveToOverview()
    fun showNotificationListing()
}