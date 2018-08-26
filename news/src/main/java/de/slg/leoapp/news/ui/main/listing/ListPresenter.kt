package de.slg.leoapp.news.ui.main.listing

import de.slg.leoapp.news.data.NewsDataManager
import de.slg.leoapp.news.data.db.Author
import de.slg.leoapp.news.data.db.Entry
import de.slg.leoapp.news.ui.main.listing.adapter.IEntryView
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.core.utility.User
import de.slg.leoapp.news.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class ListPresenter : AbstractPresenter<IListView, NewsDataManager>(), IListPresenter {

    private lateinit var entries: List<Pair<Entry, Author>>

    override fun onViewAttached(view: IListView) {
        super.onViewAttached(view)
        launch(UI) {
            entries = async(CommonPool) { getDataManager().getCurrentEntries() }.await()
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
        holder.setContent(entry.first.description)
    }

    override fun onCardClick(index: Int) {
        //todo
        getMvpView().getCallingActivity().showEntry(entries[index])
    }
}