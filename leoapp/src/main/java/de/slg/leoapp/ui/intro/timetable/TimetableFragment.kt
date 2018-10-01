package de.slg.leoapp.ui.intro.timetable

import de.slg.leoapp.R
import de.slg.leoapp.ui.intro.IntroFragment

class TimetableFragment : IntroFragment() {

    override fun getContentView() = R.layout.leoapp_fragment_intro_timetable

    override fun getFragmentTag() = "leoapp_fragment_intro_timetable"

    override fun canContinue(): Boolean {
        return true
    }

    override fun getErrorMessage() = "Bitte wähle deine Stunden aus."
}