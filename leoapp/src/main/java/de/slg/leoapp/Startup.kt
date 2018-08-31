package de.slg.leoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.slg.leoapp.ui.settings.SettingsActivity
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.ui.home.HomeActivity
import de.slg.leoapp.ui.profile.ProfileActivity

class Startup : Activity() {

    @Modules("exams", "news", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //registering all necessary information in utils so that all modules may access it without exposing the ModuleLoader
        Utils.Activity.registerProfileActivity(ProfileActivity::class.java)
        Utils.Activity.registerSettingsActivity(SettingsActivity::class.java)
        //Utils.Network.registerAPIKeyAlgorithm(ModuleLoader.getAuthenticationModule()::getAPIKey)

        Utils.Menu.addMenuEntry(R.string.app_name, getString(R.string.home), R.drawable.ic_startseite, HomeActivity::class.java)

        for (feature in ModuleLoader.getFeatures()) {
            //if (User(applicationContext!!).permission >= feature.getNecessaryPermission()) {
                Utils.Menu.addMenuEntry(feature.getFeatureId(), getString(feature.getName()), feature.getIcon(), feature.getEntryActivity())
            //}
        }

        //Terminate Splashscreen
        startActivity(Intent(applicationContext, HomeActivity::class.java))
    }

}