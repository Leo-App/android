package de.leoappslg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import de.leoappslg.core.utility.Utils
import de.leoappslg.annotation.Modules
import de.leoappslg.core.activity.LeoAppFeatureActivity
import de.leoappslg.exams.MainActivity

class Startup : Activity() {

    @Modules("exams", "survey", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LeoAppFeatureActivity.navigationMenuId = R.menu.navigation

        Utils.setup(applicationContext)
        Log.d("mox", ModuleLoader.getAuthenticationModule().toString())

        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}