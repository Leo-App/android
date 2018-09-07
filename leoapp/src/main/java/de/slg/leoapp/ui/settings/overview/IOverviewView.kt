package de.slg.leoapp.ui.settings.overview

import de.slg.leoapp.core.ui.mvp.MVPView

interface IOverviewView : MVPView {
    fun openNotificationSettings()
    fun openContact()
    fun openAbout()
    fun terminate()
}