package de.slg.leoapp.ui.settings.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.slg.leoapp.R
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import de.slg.leoapp.ui.settings.about.dialog.TeamDialog
import kotlinx.android.synthetic.main.fragment_settings_about.*

class AboutFragment : Fragment(), IAboutView {

    private lateinit var presenter: AboutPresenter
    private var toast: Toast? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = AboutPresenter()
        return inflater.inflate(R.layout.fragment_settings_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onViewAttached(this)
        initClickBehavior()
    }

    override fun openDialog() {
        TeamDialog(context!!).show()
    }

    override fun sendToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT).also { it.show() }
    }

    override fun openWebpage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.leoapp_website)))
        startActivity(browserIntent)
    }

    override fun moveToOverview() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    override fun getViewContext() = context!!

    private fun initClickBehavior() {
        arrow_back.setOnClickListener { presenter.onBackPressed() }
        initLicenceListeners()
        initVersionListeners()
        initTeamListeners()
        initWebListeners()
    }

    private fun initWebListeners() {
        val listener = { _: View -> presenter.onWebsiteClicked() }
        app_name.setOnClickListener(listener)
        school_name.setOnClickListener(listener)
    }

    private fun initVersionListeners() {
        val listener = { _: View -> presenter.onVersionClicked() }
        version.setOnClickListener(listener)
        description_version.setOnClickListener(listener)
    }

    private fun initTeamListeners() {
        val listener = { _: View -> presenter.onMembersClicked() }
        team.setOnClickListener(listener)
        description_team.setOnClickListener(listener)
    }

    private fun initLicenceListeners() {
        val listener = { _: View -> presenter.onLicenseClicked() }
        licences.setOnClickListener(listener)
        chevron_license.setOnClickListener(listener)
    }

}