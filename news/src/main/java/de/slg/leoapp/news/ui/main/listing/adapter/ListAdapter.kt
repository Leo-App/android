package de.slg.leoapp.news.ui.main.listing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.slg.leoapp.core.data.ProfilePicture
import de.slg.leoapp.news.R
import de.slg.leoapp.news.ui.main.listing.IListPresenter

//Adapter for entry list
class ListAdapter(private val presenter: IListPresenter) : RecyclerView.Adapter<ListAdapter.EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item_entry_listing, parent, false))
    }

    override fun getItemCount() = presenter.getEntryCount()

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        presenter.onBindEntryViewAtPosition(position, holder) //Let the presenter do the work
        holder.view.setOnClickListener { presenter.onCardClick(position) }
    }

    //View holder for entry items, is a View in the MVP hierarchy thus implements IEntryView
    class EntryViewHolder(val view: View) : RecyclerView.ViewHolder(view), IEntryView {

        override fun setAuthor(author: String) {
            view.findViewById<TextView>(R.id.author).text = author
        }

        override fun setAuthor(author: Int) { //Two different methods to set the author to enable translation via strings.xml
            view.findViewById<TextView>(R.id.author).setText(author)
        }

        override fun setViewCounter(views: String) {
            view.findViewById<View>(R.id.profilePicture).visibility = View.GONE
            view.findViewById<TextView>(R.id.viewCounter).text = views
        }

        override fun setProfilePicture(picture: ProfilePicture) {
            view.findViewById<View>(R.id.viewCounter).visibility = View.GONE
            if (picture.getPictureOrNull() == null) { //If the profile picture is not yet downloaded, use a placeholder
                view.findViewById<ImageView>(R.id.profilePicture).setImageBitmap(picture.getPictureOrPlaceholder())
                ProfilePicture(picture.getURLString()) { //Callback for downloaded image
                    view.findViewById<ImageView>(R.id.profilePicture)
                            .also { view -> view.setImageBitmap(it) }
                            .also { view -> view.invalidate() }
                }
            } else {
                view.findViewById<ImageView>(R.id.profilePicture).setImageBitmap(picture.getPictureOrPlaceholder())
            }
        }

        override fun setTitle(title: String) {
            view.findViewById<TextView>(R.id.titleView).text = title
        }

        override fun setContent(content: String) {
            view.findViewById<TextView>(R.id.content).text = content
        }

    }

}