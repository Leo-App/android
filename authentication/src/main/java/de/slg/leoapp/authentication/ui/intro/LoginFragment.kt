package de.slg.leoapp.authentication.ui.intro

import de.slg.leoapp.authentication.R
import de.slg.leoapp.core.ui.intro.IntroFragment

class LoginFragment : IntroFragment() {

    override fun getNextButton() = R.id.next

    override fun getContentView() = R.layout.authentication_fragment_intro_login

    override fun getFragmentTag() = "authentication_fragment_intro_login"

    override fun canContinue(): Boolean {
        return true
    }

    override fun complete() {
        super.complete()
        //TODO Authentication-Stuff
    }

    override fun getErrorMessage() = "Bitte gib einen Benutzernamen und ein Passwort ein."
}