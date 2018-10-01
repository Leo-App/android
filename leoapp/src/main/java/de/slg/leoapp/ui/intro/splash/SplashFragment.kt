package de.slg.leoapp.ui.intro.splash

import de.slg.leoapp.R
import de.slg.leoapp.ui.intro.IntroFragment

class SplashFragment : IntroFragment() {

    override fun getContentView() = R.layout.leoapp_fragment_intro_splash

    override fun getFragmentTag() = "leoapp_fragment_intro_splash"

    override fun canContinue(): Boolean {
        return true
    }

    override fun getErrorMessage() = ""
}