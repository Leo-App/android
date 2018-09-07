package de.slg.leoapp.ui.settings.overview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.slg.leoapp.R
import de.slg.leoapp.ui.settings.about.AboutFragment
import de.slg.leoapp.ui.settings.notification.NotificationFragment
import kotlinx.android.synthetic.main.fragment_settings_overview.*

class OverviewFragment : Fragment(), IOverviewView {

    private lateinit var presenter: OverviewPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = OverviewPresenter()
        return inflater.inflate(R.layout.fragment_settings_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onViewAttached(this)
        initClickBehavior()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun openNotificationSettings() {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.fragment_container, NotificationFragment(), "notification")
                ?.commit()
    }

    override fun openContact() {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:${getString(R.string.leoapp_email)}")
        intent.data = data
        startActivity(intent)
    }

    override fun openAbout() {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.fragment_container, AboutFragment(), "about")
                ?.commit()
    }

    override fun terminate() {
        activity?.finish()
    }

    override fun getViewContext() = context!!

    private fun initClickBehavior() {
        arrow_back.setOnClickListener { presenter.onBackPressed() }
        initAboutListeners()
        initContactListeners()
        initNotificationListeners()
    }

    private fun initNotificationListeners() {
        val listener = { _: View -> presenter.onNotificationClicked() }
        notifications.setOnClickListener(listener)
        description_notifications.setOnClickListener(listener)
    }

    private fun initAboutListeners() {
        val listener = { _: View -> presenter.onAboutClicked() }
        about.setOnClickListener(listener)
        description_about.setOnClickListener(listener)
    }

    private fun initContactListeners() {
        val listener = { _: View -> presenter.onContactClicked() }
        contact.setOnClickListener(listener)
        description_contact.setOnClickListener(listener)
    }

}