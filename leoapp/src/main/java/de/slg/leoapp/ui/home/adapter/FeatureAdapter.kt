package de.slg.leoapp.ui.home.adapter

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        Handler().postDelayed(Runnable {
            Log.d("leoapp", "height: ${holder.card.height}")
            Log.d("leoapp", "width: ${holder.card.width}")
            Log.d("leoapp", "mHeight: ${holder.card.measuredHeight}")
            Log.d("leoapp", "mWidth: ${holder.card.measuredWidth}")
        }, 500)
    }

    class ViewHolder(val card: View) : RecyclerView.ViewHolder(card), FeatureView {

        override fun setIcon(icon: Int) {
            card.iconView.setImageResource(icon)
        }

        override fun setName(text: Int) {
            card.featureNameView.setText(text)
        }

    }

}