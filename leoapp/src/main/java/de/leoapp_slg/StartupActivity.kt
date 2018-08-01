package de.leoapp_slg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.leoapp_slg.core.activity.LeoAppFeatureActivity
import de.leoapp_slg.exams.MainActivity

class StartupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LeoAppFeatureActivity.navigationMenuId = R.menu.navigation

        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}