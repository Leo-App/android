package de.slg.leoapp.ui.settings.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.slg.leoapp.R
import de.slg.leoapp.ui.settings.notification.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.fragment_settings_notifications.*

class NotificationFragment : Fragment(), INotificationView {

    private lateinit var presenter: NotificationPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onViewAttached(this)
    }

    override fun onDestroy() {
        presenter.onQuit()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        super.onDestroy()
    }

    override fun moveToOverview() {
        presenter.onQuit()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    override fun showNotificationListing() {
        notifications_listing.adapter = NotificationAdapter(presenter)
        notifications_listing.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    override fun getViewContext() = context!!
}