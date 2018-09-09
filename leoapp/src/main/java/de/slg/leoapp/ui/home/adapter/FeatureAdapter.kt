package de.slg.leoapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.R
import de.slg.leoapp.ui.home.IHomePresenter

class FeatureAdapter(private val presenter: IHomePresenter) : RecyclerView.Adapter<FeatureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feature_card, parent, false))
    }

    override fun getItemCount() = presenter.getModuleCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindFeatureViewAtPosition(position, holder)
        holder.card.setOnClickListener {
            presenter.onFeatureCardClicked(position)
        }
    }

    class ViewHolder(val card: View) : RecyclerView.ViewHolder(card), FeatureView {

        override fun setIcon(icon: Int) {
            card.findViewById<ImageView>(R.id.iconView).setImageResource(icon)
        }

        override fun setName(text: Int) {
            card.findViewById<TextView>(R.id.featureNameView).setText(text)
        }

    }

}