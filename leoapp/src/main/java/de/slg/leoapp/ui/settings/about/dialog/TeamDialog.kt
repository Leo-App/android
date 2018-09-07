package de.slg.leoapp.ui.settings.about.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import de.slg.leoapp.R
import kotlinx.android.synthetic.main.dialog_team.*

class TeamDialog(context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_team)
        initButtonBehaviour()
    }

    private fun initButtonBehaviour() {
        button_contact.setOnClickListener {
            //TODO contact
        }
        button_ok.setOnClickListener {
            dismiss()
        }
    }
}