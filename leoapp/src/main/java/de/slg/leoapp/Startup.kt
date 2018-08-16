package de.slg.leoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.utility.Utils

import de.slg.leoapp.exams.MainActivity

class Startup : Activity() {

    @Modules("exams", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.setup(applicationContext)

        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}