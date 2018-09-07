package de.slg.leoapp.ui.settings.about

import android.os.Handler
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class AboutPresenter : AbstractPresenter<IAboutView, Unit>(), IAboutPresenter {

    override fun onWebsiteClicked() {
        getMvpView().openWebpage()
    }

    override fun onVersionClicked() {
        when (ClickHandler.clicks) {
            0, 1 -> ClickHandler.startTimer()
            2, 3, 4 -> {
                ClickHandler.startTimer()
                getMvpView().sendToast("${5-ClickHandler.clicks} Steps left")
            }
            5 -> {
                //TODO implement
                getMvpView().sendToast("wow")
            }
        }

        ClickHandler.clicks++
    }

    override fun onMembersClicked() {
        getMvpView().openDialog()
    }

    override fun onLicenseClicked() {
        //TODO implement
    }

    override fun onBackPressed() {
        getMvpView().moveToOverview()
    }

    private abstract class ClickHandler {
        companion object {
            private const val clickTimeout: Long = 1500L

            internal var clicks: Int = 0
            internal var countdown: Handler? = null

            fun startTimer() {
                countdown?.removeCallbacksAndMessages(null)
                countdown = countdown ?: Handler()
                countdown?.postDelayed({
                    clicks = 0
                }, clickTimeout)
            }

        }
    }

}