package de.slg.leoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.slg.leoapp.ui.settings.SettingsActivity
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.utility.User
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.ui.home.HomeActivity
import de.slg.leoapp.ui.profile.ProfileActivity

class Startup : Activity() {

    @Modules("exams", "news", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.Activity.registerProfileActivity(ProfileActivity::class.java)
        Utils.Activity.registerSettingsActivity(SettingsActivity::class.java)

        for (feature in ModuleLoader.getFeatures()) {
            //if (User(applicationContext!!).permission >= feature.getNecessaryPermission()) {
                Utils.Menu.addMenuEntry(feature.getFeatureId(), getString(feature.getName()), feature.getIcon(), feature.getEntryActivity())
            //}
        }

        startActivity(Intent(applicationContext, HomeActivity::class.java))
    }

}