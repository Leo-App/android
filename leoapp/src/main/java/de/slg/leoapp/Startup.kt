package de.slg.leoapp

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Interpolator
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.core.utility.pxValue
import de.slg.leoapp.data.FeatureDataManager
import de.slg.leoapp.ui.home.HomeActivity
import de.slg.leoapp.ui.profile.ProfileActivity
import de.slg.leoapp.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlin.math.PI
import kotlin.math.sin


class Startup : Activity() {

    private lateinit var animation: ValueAnimator

    @Modules("exams", "news", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        startSplashAnimation()

        //registering all necessary information in utils so that all modules may access it without exposing the ModuleLoader
        Utils.Activity.registerProfileActivity(ProfileActivity::class.java)
        Utils.Activity.registerSettingsActivity(SettingsActivity::class.java)
        Utils.Network.registerAPIKeyAlgorithm(ModuleLoader.getAuthenticationModule()::getAPIKey)

        Utils.Menu.addMenuEntry(
                R.string.home,
                getString(R.string.home),
                R.drawable.ic_startseite,
                HomeActivity::class.java
        )

        for (feature in ModuleLoader.getFeatures()) {
            //TODO if (User(applicationContext).permission >= feature.getNecessaryPermission()) {
                Utils.Menu.addMenuEntry(
                        feature.getFeatureId(),
                        getString(feature.getName()),
                        feature.getIcon(),
                        feature.getEntryActivity()
                )

        }

        //Terminate Splashscreen
        FeatureDataManager.syncUsageStatistics { finish() }
    }

    private fun startSplashAnimation() {
        val maxSize = 45f.pxValue(applicationContext)
        animation = ValueAnimator.ofInt(1, 1).apply {
            repeatCount = Animation.INFINITE
            duration = 3500
            interpolator = Interpolator { t -> (sin(t * 2 * PI)).toFloat() }
            addUpdateListener {
                val params = app_icon.layoutParams.apply {
                    height = (animatedFraction * maxSize).toInt()
                    width = (animatedFraction * maxSize).toInt()
                }
                app_icon.layoutParams = params
            }
        }
        animation.start()
    }

    override fun finish() {

        animation.cancel()

        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, transition_view, "transition")

        val revealX = (window.decorView.width / 2)
        val revealY = (window.decorView.height / 2)

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("x", revealX)
        intent.putExtra("y", revealY)

        ActivityCompat.startActivity(this, intent, options.toBundle())
        launch(UI) {
            delay(1000)
            super.finish()
        }
    }

}