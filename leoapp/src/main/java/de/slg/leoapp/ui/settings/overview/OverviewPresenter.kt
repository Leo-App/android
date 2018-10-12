package de.slg.leoapp.ui.settings.overview

import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class OverviewPresenter : AbstractPresenter<IOverviewView, Unit>(), IOverviewPresenter {

    override fun onNotificationClicked() {
        getMvpView().openNotificationSettings()
    }

    override fun onBackPressed() {
        getMvpView().terminate()
    }

    override fun onContactClicked() {
        getMvpView().openContact()
    }

    override fun onAboutClicked() {
        getMvpView().openAbout()
    }

}