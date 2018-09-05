package de.slg.leoapp.ui.settings.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import de.slg.leoapp.R

class TeamDialog(context: Context) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_team)
        super.onCreate(savedInstanceState)
    }
}