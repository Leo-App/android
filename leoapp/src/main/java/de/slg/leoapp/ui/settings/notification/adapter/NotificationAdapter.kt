package de.slg.leoapp.ui.settings.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.R
import de.slg.leoapp.ui.settings.notification.INotificationPresenter
import kotlinx.android.synthetic.main.leoapp_item_settings_notification.view.*

class NotificationAdapter(private val presenter: INotificationPresenter) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.leoapp_item_settings_notification, parent, false))
    }

    override fun getItemCount() = presenter.getNotificationAmount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindNotificationViewAtPosition(position, holder)
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), IItemView {
        override fun getViewContext() = view.context!!
        override fun setTitle(title: Int) {
            view.title.setText(title)
        }

        override fun setDescription(description: Int) {
            view.description.setText(description)
        }

        override fun setSwitchState(state: Boolean) {
            view.toggle.isChecked = state
        }

        override fun setIcon(icon: Int) {
            view.icon.setImageResource(icon)
        }
    }
}