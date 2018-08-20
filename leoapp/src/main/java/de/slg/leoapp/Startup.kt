package de.slg.leoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.utility.Utils

import de.slg.leoapp.exams.MainActivity

class Startup : Activity() {

    @Modules("exams", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (feature in ModuleLoader.getFeatures()) {
            //if (Utils.User.permission >= feature.getNecessaryPermission()) {
            Utils.Menu.addMenuEntry(feature.getFeatureId(), getString(feature.getName()), feature.getIcon(), feature.getEntryActivity())
            //}
        }

        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}