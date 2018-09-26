package de.slg.leoapp

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.core.data.User
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.core.utility.dpToPx
import de.slg.leoapp.data.FeatureDataManager
import de.slg.leoapp.ui.home.HomeActivity
import de.slg.leoapp.ui.profile.ProfileActivity
import de.slg.leoapp.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.leoapp_splash.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlin.math.PI
import kotlin.math.sin

class Startup : Activity() {

    private lateinit var animation: ValueAnimator

    @Modules("exams", "news", "timetable", "lunch", "substitutions", authentication = "authentication")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leoapp_splash)
        launch(UI) { startSplashAnimation() }

        //registering all necessary information in utils so that all modules may access it without exposing the ModuleLoader
        Utils.Activity.registerProfileActivity(ProfileActivity::class.java)
        Utils.Activity.registerSettingsActivity(SettingsActivity::class.java)
        Utils.Network.registerAPIKeyAlgorithm(ModuleLoader.getAuthenticationModule()::getAPIKey)

        Utils.Menu.addMenuEntry(
                R.string.leoapp_home,
                getString(R.string.leoapp_home),
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

        //TODO testing remove later

        val user = User(applicationContext)
        user.firstName = "Max"
        user.lastName = "Mustermann"
        user.grade = "EF"
        user.id = 1337
        user.loginName = "maxmus110110"
        user.permission = 1
        ProfilePicture("https://images.unsplash.com/photo-1536770494015-01837601cbd4?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=923dc616ffd0fa46ecdd0d7977f867dd&auto=format&fit=crop&w=634&q=80") {
            user.profilePicture = ProfilePicture(it, user.id)
            FeatureDataManager.syncUsageStatistics { finish() }
        }

        //TODO testing remove later

        //Terminate Splashscreen
       // FeatureDataManager.syncUsageStatistics { finish() } todo add later
    }

    private suspend fun startSplashAnimation() {
        val maxAmplitude = 15f.dpToPx(applicationContext)
        val defaultSize = 75f.dpToPx(applicationContext)

        ValueAnimator.ofInt(0, defaultSize).apply {
            duration = 100
            interpolator = LinearInterpolator()
            addUpdateListener {
                val params = app_icon.layoutParams.apply {
                    val animatedValue = (defaultSize * animatedFraction).toInt()
                    height = animatedValue
                    width = animatedValue
                }
                app_icon.layoutParams = params
            }
        }.start()

        delay(100)

        animation = ValueAnimator.ofInt(defaultSize, defaultSize).apply {
            repeatCount = Animation.INFINITE
            duration = 1200
            interpolator = Interpolator { t -> (sin(t * 2 * PI)).toFloat() }
            addUpdateListener {
                val params = app_icon.layoutParams.apply {
                    val animatedValue = (defaultSize + maxAmplitude * animatedFraction).toInt()
                    height = animatedValue
                    width = animatedValue
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