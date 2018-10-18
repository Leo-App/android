package de.slg.leoapp.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import de.slg.leoapp.ModuleLoader
import de.slg.leoapp.R
import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.ui.ActionLogActivity
import de.slg.leoapp.core.ui.intro.IntroFragment
import de.slg.leoapp.ui.home.HomeActivity
import de.slg.leoapp.ui.intro.splash.SplashFragment

class IntroActivity : ActionLogActivity() {

    private val fragments: List<IntroFragment> = List(SplashFragment())

    init {
        for (fragment in ModuleLoader.getAuthenticationModule().getIntroFragments()) {
            fragments.append(fragment.newInstance())
        }
        for (feature in ModuleLoader.getFeatures()) {
            for (fragment in feature.getIntroFragments()) {
                fragments.append(fragment.newInstance())
            }
        }
    }

    private var lastToast: Toast? = null

    override fun getActivityTag() = "leoapp_activity_intro"

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.leoapp_activity_intro)

        for (fragment in fragments) {
            fragment.listener = View.OnClickListener { onNextPressed() }
        }

        print(fragments.hasAccess())

        val fragment = getNextIncompleteFragment()
        if (fragment != null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, fragment.getFragmentTag()).commit()
        } else {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 1) {
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.fragments.last()).commit()
        } else {
            finish()
        }
    }

    private fun onNextPressed() {
        if (lastToast != null) {
            lastToast!!.cancel()
        }

        val current = supportFragmentManager.fragments.last() as IntroFragment
        if (current.canContinue()) {
            current.complete()

            val fragment = getNextIncompleteFragment()
            if (fragment != null) {
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, fragment.getFragmentTag()).commit()
            } else {
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            }
        } else {
            lastToast = Toast.makeText(applicationContext, current.getErrorMessage(), Toast.LENGTH_LONG)
            lastToast!!.show()
        }
    }

    private fun getNextIncompleteFragment(): IntroFragment? {
        if (!fragments.hasAccess()) {
            fragments.toFirst()
        }

        while (fragments.hasAccess() && fragments.getContent().isCompleted(applicationContext)) {
            fragments.next()
        }

        if (fragments.hasAccess()) {
            return fragments.getContent()
        }

        return null
    }

}