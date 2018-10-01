package de.slg.leoapp.ui.intro.login

import de.slg.leoapp.R
import de.slg.leoapp.ui.intro.IntroFragment

class LoginFragment : IntroFragment() {

    override fun getContentView() = R.layout.leoapp_fragment_intro_login

    override fun getFragmentTag() = "leoapp_fragment_intro_login"

    override fun canContinue(): Boolean {
        return true
    }

    override fun getErrorMessage() = "Bitte gib einen Benutzernamen und ein Passwort ein."
}