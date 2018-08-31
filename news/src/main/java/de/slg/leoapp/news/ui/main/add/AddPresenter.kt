package de.slg.leoapp.news.ui.main.add

import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.core.ui.mvp.AbstractPresenter

class AddPresenter : AbstractPresenter<IAddView, INewsDataManager>(), IAddPresenter {
    override fun onAddFinished() {
        //todo check if successful, add to local database, make api call etc
    }
}