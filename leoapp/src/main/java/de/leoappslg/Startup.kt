package de.leoappslg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.leoappslg.core.utility.Utils
import de.leoappslg.annotation.Modules
import de.leoappslg.core.activity.LeoAppFeatureActivity
import de.leoappslg.exams.MainActivity

class Startup : Activity() {

    @Modules("exams", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LeoAppFeatureActivity.navigationMenuId = R.menu.navigation

        Utils.setup(applicationContext)

        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}