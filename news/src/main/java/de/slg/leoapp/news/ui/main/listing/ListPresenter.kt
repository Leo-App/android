package de.slg.leoapp.news.ui.main.listing

import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.core.data.User
import de.slg.leoapp.news.R
import de.slg.leoapp.news.data.NewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.listing.adapter.IEntryView

class ListPresenter : AbstractPresenter<IListView, NewsDataManager>(), IListPresenter {

    private lateinit var entries: List<Pair<Entry, Author>>

    init {
        registerDataManager(NewsDataManager)
    }

    override fun onViewAttached(view: IListView) {
        super.onViewAttached(view)
        getDataManager().getCurrentEntries {
            entries = it
            getMvpView().showListing()
        }
    }

    override fun getEntryCount() = entries.size

    override fun onBindEntryViewAtPosition(position: Int, holder: IEntryView) {
        val entry = entries[position]
        val user = User(getMvpView().getViewContext())

        if (user.id == entry.first.authorId) {
            holder.setAuthor(R.string.title_me)
            holder.setViewCounter(
                    if (entry.first.views > 999)
                        "999+"
                    else
                        entry.first.views.toString()
            )
        } else {
            holder.setAuthor(entry.second.lastName)
            holder.setProfilePicture(entry.second.profileImage)
        }

        holder.setTitle(entry.first.title)
        holder.setContent(entry.first.content)
    }

    override fun onCardClick(index: Int) {
        getMvpView().getCallingActivity().showEntry(entries[index])
    }

    override fun onCardDeleted(entry: Pair<Entry, Author>) {
        getDataManager().removeEntry(entry, getMvpView().getViewContext())

        //this is kind of resource heavy but since we don't delete often
        //and the amount of entries is usually small, the performance impact should be negligible
        val entryCopy = entries.toMutableList()
        entryCopy.remove(entry)
        entries = entryCopy
    }
}